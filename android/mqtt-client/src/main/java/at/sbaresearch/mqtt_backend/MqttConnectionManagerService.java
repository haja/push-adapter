/*
 * Copyright (c) 2020 Harald Jagenteufel.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 *
 * The Eclipse Public License is available at
 *    http://www.eclipse.org/legal/epl-v10.html.
 */

package at.sbaresearch.mqtt_backend;

import android.Manifest.permission;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import at.sbaresearch.mqtt4android.pinning.ClientKeyCert;
import at.sbaresearch.mqtt4android.pinning.PinningSslFactory;
import lombok.val;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.*;

import java.util.Objects;

import static at.sbaresearch.mqtt_backend.API.*;

public class MqttConnectionManagerService extends Service {

  public static final int NOTIFICATION_ID = 1;

  private static final String TAG = "MqttConnectionMgrSrvc";

  private MqttAndroidClient mqttAndroidClient;

  private MqttConnectOptions mqttConnectOptions;
  private IBinder binder = new MqttConnectionBinder();

  private Integer currentStartId = null;

  public MqttConnectionManagerService() {
    mqttConnectOptions = new MqttConnectOptions();
    mqttConnectOptions.setAutomaticReconnect(true);
  }

  static ConnectionSettings fromBundle(Bundle bundle) {
    return new ConnectionSettings(
        bundle.getString(INTENT_MQTT_CONNECT_HOST),
        bundle.getInt(INTENT_MQTT_CONNECT_PORT),
        bundle.getString(INTENT_MQTT_CONNECT_TOPIC),
        bundle.getByteArray(INTENT_MQTT_CONNECT_CLIENT_KEY),
        bundle.getByteArray(INTENT_MQTT_CONNECT_CLIENT_CERT)
    );
  }

  @Override
  public void onCreate() {
    super.onCreate();

    if (android.os.Build.VERSION.SDK_INT >= VERSION_CODES.O) {
        if (ContextCompat.checkSelfPermission(this, permission.FOREGROUND_SERVICE)
            != PackageManager.PERMISSION_GRANTED) {
          Log.i(TAG, "cannot start in foreground, no permission");
        } else {
          Log.i(TAG, "starting in foreground");
          val notif = new Notification.Builder(this, MqttBackendConfigActivity.LOW_PRIO_CHANNEL_ID)
              .setContentTitle("push connection open")
              .build();
          startForeground(NOTIFICATION_ID, notif);
        }
    }
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.i(TAG, "onStartCommand: intent: " + intent + " startId " + startId);
    if (intent == null) {
      Log.i(TAG, "* onStartCommand: intent is null, we were restarted");
      resetStartId(startId);
      Log.i(TAG, "onStartCommand finished START_STICKY; after resetStartId");
      return START_STICKY;
    }
    doDisconnect(currentStartId, (msg) -> {
      Log.i(TAG, "onStartCommand callback: startId next");
      Log.i(TAG, "onStartCommand callback: startId " + startId);
      currentStartId = startId;
      ConnectionSettings s = fromBundle(Objects.requireNonNull(intent.getExtras()));
      this.connect(s);
      Log.i(TAG, "onStartCommand callback finished");
      return true;
    });
    Log.i(TAG, "onStartCommand finished START_STICKY");
    return START_STICKY;
  }

  private void resetStartId(int startId) {
    if (startId == currentStartId) {
      Log.i(TAG, "resetStartId: current and previous startId match, no-op");
      return;
    }
    stopSelf(currentStartId);
    currentStartId = startId;
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    Log.i(TAG, "onBind: intent: " + intent);
    return binder;
  }

  @Override
  public void onDestroy() {
    Log.i(TAG, "onDestroy");
    doDisconnect(currentStartId, (msg) -> true);
    currentStartId = null;
  }

  private void connect(ConnectionSettings sett) {
    Log.d(TAG, "connect called");
    if (mqttAndroidClient != null) {
      Log.e(TAG, "connect: still connected, disconnect first!",
          new RuntimeException("mqtt still connected"));
    }

    setupSsl(sett);
    createClient(sett);
    Log.d(TAG, "startId " + this.currentStartId + " connecting to " + sett.getServerUrl());

    final TopicSubscriber subscriber = new TopicSubscriber(mqttAndroidClient, sett);
    MqttCallback recvCallback = new ReceiveCallback(this, currentStartId, subscriber);
    mqttAndroidClient.setCallback(recvCallback);

    final IMqttActionListener connectCb = new IMqttActionListener() {
      @Override
      public void onSuccess(IMqttToken asyncActionToken) {
        Log.i(TAG, "initial connection established to: " + sett);
      }

      @Override
      public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
        Throwable cause = exception.getCause();
        String causeMsg = cause == null ? "" : " cause: " + cause.getMessage();
        Log.e(TAG, "initial connection failed: " + exception.getMessage() + causeMsg);
      }
    };
    AsyncTask<Void, Void, Void> connectTask =
        new ConnectTask(mqttConnectOptions, connectCb, mqttAndroidClient);
    connectTask.execute();
  }

  private void setupSsl(ConnectionSettings settings) {
    try {
      val keys = new ClientKeyCert(settings.getPrivKey(), settings.getCert());
      mqttConnectOptions.setSocketFactory(new PinningSslFactory(keys,
          getApplicationContext().getResources().openRawResource(R.raw.server)).getSocketFactory());
    } catch (Exception e) {
      Log.e(TAG, "onCreate: sslSocketFactorySetup failed", e);
      throw new SslSetupException(e);
    }
  }

  private void createClient(ConnectionSettings sett) {
    Log.d(TAG, "createClient: creating...");
    mqttAndroidClient = new MqttAndroidClient(this, sett.getServerUrl(), sett.getTopic());
  }

  public void disconnect() {
    doDisconnect(currentStartId, (msg) -> true);
    currentStartId = null;
  }

  private void doDisconnect(Integer forStartId, Callback cb) {
    Log.d(TAG, "doDisconnect for startId " + forStartId);
    if (forStartId == null) {
      Log.d(TAG, "doDisconnect: startId provided is null");
    }
    if (mqttAndroidClient != null) {
      try {
        final IMqttToken token = mqttAndroidClient.disconnect(0);
        val self = this;
        token.setActionCallback(new IMqttActionListener() {
          @Override
          public void onSuccess(IMqttToken asyncActionToken) {
            Log.i(TAG, "doDisconnect::onSuccess");
            closeMqttClient();
            if (forStartId != null) {
              self.stopSelf(forStartId);
            }
            cb.handleMessage(null);
          }

          @Override
          public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
            Log.e(TAG, "doDisconnect::onFailure");
            closeMqttClient();
            if (forStartId != null) {
              self.stopSelf(forStartId);
            }
            cb.handleMessage(null);
          }
        });
      } catch (MqttException e) {
        Log.e(TAG, "doDisconnect: failed ", e);
        closeMqttClient();
        if (forStartId != null) {
          stopSelf(forStartId);
        }
        cb.handleMessage(null);
      }
    } else {
      Log.d(TAG, "doDisconnect: mqttClient was null");
      if (forStartId != null) {
        stopSelf(forStartId);
      }
      cb.handleMessage(null);
    }
    Log.d(TAG, "doDisconnect done");
  }

  private void closeMqttClient() {
    if (mqttAndroidClient != null) {
      mqttAndroidClient.unregisterResources();
      mqttAndroidClient.close();
    }
    mqttAndroidClient = null;
  }

  private static class ConnectTask extends AsyncTask<Void, Void, Void> {
    private final MqttConnectOptions options;
    private final IMqttActionListener connectCb;
    private final MqttAndroidClient mqttAndroidClient;

    public ConnectTask(MqttConnectOptions options, IMqttActionListener connectCb,
        MqttAndroidClient mqttAndroidClient) {
      this.options = options;
      this.connectCb = connectCb;
      this.mqttAndroidClient = mqttAndroidClient;
    }

    @Override
    protected Void doInBackground(Void... voids) {
      try {
        mqttAndroidClient.connect(options, null, connectCb)
            .waitForCompletion();
      } catch (MqttException e) {
        Log.e(TAG, "connection failed" + e.getMessage());
      }
      Log.i(TAG, "connect: finished");
      return null;
    }
  }

  public class MqttConnectionBinder extends Binder {
    public MqttConnectionManagerService getService() {
      return MqttConnectionManagerService.this;
    }
  }

  private class SslSetupException extends RuntimeException {
    public SslSetupException(Exception e) {
      super(e);
    }
  }
}
