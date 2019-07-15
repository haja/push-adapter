/*
 * Copyright (C) 2013-2017 microG Project Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package at.sbaresearch.microg.adapter.library.firebase.messaging;

import android.Manifest.permission;
import android.app.*;
import android.app.Notification.Builder;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.support.v4.os.AsyncTaskCompat;
import android.util.Log;
import at.sbaresearch.microg.adapter.library.gms.common.PublicApi;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static at.sbaresearch.microg.adapter.library.gms.gcm.GcmConstants.*;

/**
 * Base class for receiving Firebase Messages.
 * Override base class methods to handle any events required by the application.
 * Methods are invoked asynchronously.
 * // TODO This is wrong, update usage of FirebaseMessagingService
 * <p/>
 * Include the following in the manifest:
 * <pre>
 * <service
 *     android:name=".YourFcmListenerService"
 *     android:exported="false" >
 *     <intent-filter>
 *         <action android:name="at.sbaresearch.android.c2dm.intent.RECEIVE" />
 *     </intent-filter>
 * </service></pre>
 * on android O and higher, requires
 * <pre>
 *   <uses-permission android:name="android.permission.FOREGROUND_SERVICE"/>
 * </pre>
 */
@PublicApi
public abstract class FirebaseMessagingService extends Service {
  private static final String TAG = "FcmListenerService";

  private static final String FALLBACK_LOW_PRIO_CHANNEL_ID = "lowChannelId";
  private static final int NOTIFICATION_ID = 3011;

  private final Object lock = new Object();
  private int startId;
  private int counter = 0;

  public final IBinder onBind(Intent intent) {
    return null;
  }

  /**
   * Called when a message is received.
   */
  public void onMessageReceived(RemoteMessage message) {
    // To be overwritten
  }

  /**
   * Called when a new token is generated
   * TODO implement onNewToken
   */
  public void onNewToken(String token) {
    // to be overwritten
  }

  /**
   * NOT SUPPORTED
   */
  public void onDeletedMessages() {
    // To be overwritten
  }

  /**
   * NOT SUPPORTED
   */
  public void onMessageSent(String msgId) {
    // To be overwritten
  }

  /**
   * NOT SUPPORTED
   */
  public void onSendError(String msgId, String error) {
    // To be overwritten
  }

  public final int onStartCommand(final Intent intent, int flags, int startId) {
    synchronized (lock) {
      this.startId = startId;
      this.counter++;
    }

    if (Build.VERSION.SDK_INT >= VERSION_CODES.O) {
      // TODO the version_codes check uses API level of this lib,
      //  not the actual app API level which uses this lib; how to fix this?
      if (Build.VERSION.SDK_INT >= VERSION_CODES.P && ContextCompat.checkSelfPermission(this, permission.FOREGROUND_SERVICE)
          != PackageManager.PERMISSION_GRANTED) {
        Log.w(TAG,
            "no permission, but trying to start in foreground anyways (might be running on android O and < P");
      }
      Log.i(TAG, "starting in foreground");
      Notification notif = new Builder(this, getOrCreateNotificationChannel())
          .setContentTitle("receiving push message")
          .setContentText("receiving push message")
          .build();
      startForeground(NOTIFICATION_ID, notif);
    }

    if (intent != null) {
      if (ACTION_NOTIFICATION_OPEN.equals(intent.getAction())) {
        handlePendingNotification(intent);
        finishCounter();
        // TODO stop foreground service?
        // GcmReceiver.completeWakefulIntent(intent);
      } else if (ACTION_C2DM_RECEIVE.equals(intent.getAction())) {
        AsyncTaskCompat.executeParallel(new AsyncTask<Intent, Void, Void>() {
          @Override
          protected Void doInBackground(Intent... params) {
            handleC2dmMessage(params[0]);
            return null;
          }
        }, intent);
      } else {
        Log.w(TAG, "Unknown intent action: " + intent.getAction());

      }

      return START_REDELIVER_INTENT;
    } else {
      finishCounter();
      return START_NOT_STICKY;
    }
  }

  private void handleC2dmMessage(Intent intent) {
    try {
      String messageType = intent.getStringExtra(EXTRA_MESSAGE_TYPE);
      if (messageType == null || MESSAGE_TYPE_GCM.equals(messageType)) {
        String from = intent.getStringExtra(EXTRA_FROM);
        long sent = intent.getLongExtra(EXTRA_SENT_TIME, 0L);
        Bundle data = intent.getExtras();
        // TODO cleanup
        data.remove(EXTRA_MESSAGE_TYPE);
        data.remove(
            "android.support.content.wakelockid"); // WakefulBroadcastReceiver.EXTRA_WAKE_LOCK_ID
        data.remove(EXTRA_FROM);
        data.remove(EXTRA_SENT_TIME);
        String msgId = intent.getStringExtra(EXTRA_MESSAGE_ID);
        // TODO acquire + hold wakelock here? for ~10sec max, as this is IIRC what FCM assures?
        //  or hold wakelock in other app (push-adapter) which has already doze exception?
        onMessageReceived(buildRemoteMessage(from, data.getString(EXTRA_PAYLOAD), msgId, sent));
        // TODO how to release wakelock early (before 10 sec over, if app already finished processing)?
        // TODO add if for onNewToken
        // TODO delete not supported message types
      } else if (MESSAGE_TYPE_DELETED_MESSAGE.equals(messageType)) {
        onDeletedMessages();
      } else if (MESSAGE_TYPE_SEND_EVENT.equals(messageType)) {
        onMessageSent(intent.getStringExtra(EXTRA_MESSAGE_ID));
      } else if (MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
        onSendError(intent.getStringExtra(EXTRA_MESSAGE_ID), intent.getStringExtra(EXTRA_ERROR));
      } else {
        Log.w(TAG, "Unknown message type: " + messageType);
      }
      finishCounter();
    } finally {
      // TODO stop foreground service?
      // GcmReceiver.completeWakefulIntent(intent);
    }
  }

  private RemoteMessage buildRemoteMessage(String from, String dataAsString,
      String messageId, long sentTime) {
    Map<String, String> asMap = new HashMap<>();
    try {
      asMap = parseData(new JSONObject(dataAsString));
    } catch (JSONException e) {
      Log.e(TAG, "buildRemoteMessage: cannot parse json string", e);
    }
    return new RemoteMessage(from, asMap, messageId, sentTime);
  }

  private Map<String, String> parseData(JSONObject data) {
    HashMap<String, String> map = new HashMap<>();
    Iterator<String> it = data.keys();
    while (it.hasNext()) {
      String key = it.next();
      map.put(key, data.optString(key));
    }
    return map;
  }

  private String getOrCreateNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationManager notificationManager = getSystemService(NotificationManager.class);
      for (NotificationChannel channel : notificationManager.getNotificationChannels()) {
        if (channel.getImportance() == NotificationManager.IMPORTANCE_LOW) {
          Log.i(TAG, "getOrCreateNotificationChannel: reusing notification channel " + channel.getId());
          return channel.getId();
        }
      }
      return createNotificationChannel();
    }
    return null;
  }

  private String createNotificationChannel() {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      Log.i(TAG, "createNotificationChannel: creating notification channel");
      CharSequence name = "permanentNotification";
      String description = "permanentNotification";
      int importance = NotificationManager.IMPORTANCE_LOW;
      NotificationChannel channel = new NotificationChannel(FALLBACK_LOW_PRIO_CHANNEL_ID, name, importance);
      channel.setDescription(description);
      // Register the channel with the system; you can't change the importance
      // or other notification behaviors after this
      NotificationManager notificationManager = getSystemService(NotificationManager.class);
      notificationManager.createNotificationChannel(channel);
      return  FALLBACK_LOW_PRIO_CHANNEL_ID;
    }
    return null;
  }


  private void handlePendingNotification(Intent intent) {
    PendingIntent pendingIntent = intent.getParcelableExtra(EXTRA_PENDING_INTENT);
    if (pendingIntent != null) {
      try {
        pendingIntent.send();
      } catch (PendingIntent.CanceledException e) {
        Log.w(TAG, "Notification cancelled", e);
      }
    } else {
      Log.w(TAG, "Notification was null");
    }
  }

  private void finishCounter() {
    synchronized (lock) {
      this.counter--;
      if (counter == 0) {
        stopSelfResult(startId);
      }
    }
  }

}