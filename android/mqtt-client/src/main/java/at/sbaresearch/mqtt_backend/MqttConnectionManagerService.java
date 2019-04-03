package at.sbaresearch.mqtt_backend;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.*;

import java.util.Objects;

public class MqttConnectionManagerService extends Service {

  private static final String TAG = "MqttConnectionMgrSrvc";

  private MqttAndroidClient mqttAndroidClient;
  // TODO receive connection details on connect intent
  private String clientId = "test1";

  private MqttCallback recvCallback;

  private MqttConnectOptions mqttConnectOptions;
  private IBinder binder = new MqttConnectionBinder();

  public MqttConnectionManagerService() {
    mqttConnectOptions = new MqttConnectOptions();
    mqttConnectOptions.setAutomaticReconnect(true);
  }

  @Override
  public void onCreate() {
    super.onCreate();

    //android.os.Debug.waitForDebugger();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.i(TAG, "onStartCommand: intent: " + intent);
    ConnectionSettings s = ConnectionSettings.fromBundle(Objects.requireNonNull(intent.getExtras()));
    this.connect(s);
    return START_STICKY;
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return binder;
  }

  @Override
  public void onDestroy() {
    disconnect();
  }

  private void connect(ConnectionSettings sett) {
    Log.d(TAG, "connect called");

    setupSsl(sett);

    ensureClientExists(sett);
    if (!this.mqttAndroidClient.isConnected()) {
      Log.d(TAG, "connecting to " + sett.getServerUrl());

      recvCallback = new ReceiveCallback(this);
      mqttAndroidClient.setCallback(recvCallback);
      final IMqttActionListener connectCb = new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken asyncActionToken) {
          final String msg = "connection established";
          Log.i(TAG, msg);
          // Snackbar.make(view, "connection established", Snackbar.LENGTH_SHORT)
          // .setAction("Action", null).show();
          try {
            mqttAndroidClient.subscribe(sett.getTopic(), 0);
          } catch (MqttException e) {
            String msgFail = "subscribe failed: " + e.getMessage();
            Log.e(TAG, msgFail);
            // Snackbar.make(view, msgFail, Snackbar.LENGTH_LONG)
            // .setAction("Action", null).show();
          }
        }

        @Override
        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
          Throwable cause = exception.getCause();
          String causeMsg = cause == null ? "" : cause.getMessage();
          Log.e(TAG, "connect failed: " + exception.getMessage() + causeMsg);
          // Snackbar.make(view, "connect failed", Snackbar.LENGTH_SHORT)
          // .setAction("Action", null).show();
        }
      };
      AsyncTask<Void, Void, Void> connectTask =
          new VoidVoidVoidAsyncTask(mqttConnectOptions, connectCb, mqttAndroidClient);
      connectTask.execute();
    }
  }

  private void setupSsl(ConnectionSettings settings) {
    try {
      mqttConnectOptions.setSocketFactory(new PinningSslFactory(getApplicationContext(), settings).getSocketFactory());
    } catch (Exception e) {
      Log.e(TAG, "onCreate: sslSocketFactorySetup failed", e);
      throw new RuntimeException(e);
    }
  }

  private void ensureClientExists(ConnectionSettings sett) {
    if (mqttAndroidClient == null) {
      mqttAndroidClient = new MqttAndroidClient(this, sett.getServerUrl(), clientId);
    }
  }

  public void disconnect() {
    if (mqttAndroidClient != null) {
      try {
        final IMqttToken token = mqttAndroidClient.disconnect(0);
        token.setActionCallback(new IMqttActionListener() {
          @Override
          public void onSuccess(IMqttToken asyncActionToken) {
            Log.i(TAG, "disconnect::onSuccess");
            mqttAndroidClient.close();
            mqttAndroidClient = null;
          }

          @Override
          public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
            Log.e(TAG, "disconnect::onFailure");
            mqttAndroidClient.close();
            mqttAndroidClient = null;
          }
        });
      } catch (MqttException e) {
        Log.e(TAG, "disconnect: failed ", e);
      }
    }
  }

  private static class VoidVoidVoidAsyncTask extends AsyncTask<Void, Void, Void> {
    private final MqttConnectOptions options;
    private final IMqttActionListener connectCb;
    private final MqttAndroidClient mqttAndroidClient;

    public VoidVoidVoidAsyncTask(MqttConnectOptions options, IMqttActionListener connectCb,
        MqttAndroidClient mqttAndroidClient) {
      this.options = options;
      this.connectCb = connectCb;
      this.mqttAndroidClient = mqttAndroidClient;
    }

    @Override
    protected Void doInBackground(Void... voids) {
      try {
        this.mqttAndroidClient.connect(options, null, connectCb);
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
