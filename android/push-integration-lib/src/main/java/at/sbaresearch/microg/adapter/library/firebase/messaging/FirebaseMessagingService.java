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

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.os.AsyncTaskCompat;
import android.util.Log;
import at.sbaresearch.microg.adapter.library.gms.common.PublicApi;
import at.sbaresearch.microg.adapter.library.gms.gcm.GcmReceiver;

import java.util.HashMap;
import java.util.Map;

import static at.sbaresearch.microg.adapter.library.gms.gcm.GcmConstants.*;

/**
 * Base class for receiving Firebase Messages.
 * Override base class methods to handle any events required by the application.
 * Methods are invoked asynchronously.
 * // TODO This is wrong
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
 */
@PublicApi
public abstract class FirebaseMessagingService extends Service {
  private static final String TAG = "FcmListenerService";

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

    if (intent != null) {
      if (ACTION_NOTIFICATION_OPEN.equals(intent.getAction())) {
        handlePendingNotification(intent);
        finishCounter();
        GcmReceiver.completeWakefulIntent(intent);
      } else if (ACTION_C2DM_RECEIVE.equals(intent.getAction())) {
        AsyncTaskCompat.executeParallel(new AsyncTask<Void, Void, Void>() {
          @Override
          protected Void doInBackground(Void... params) {
            handleC2dmMessage(intent);
            return null;
          }
        });
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
        Bundle data = intent.getExtras();
        data.remove(EXTRA_MESSAGE_TYPE);
        data.remove(
            "android.support.content.wakelockid"); // WakefulBroadcastReceiver.EXTRA_WAKE_LOCK_ID
        data.remove(EXTRA_FROM);
        onMessageReceived(buildRemoteMessage(from, data));
        // TODO add if for onNewToken
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
      GcmReceiver.completeWakefulIntent(intent);
    }
  }

  private RemoteMessage buildRemoteMessage(String from, Bundle data) {
    Map<String, String> asMap = new HashMap<>(data.size());
    for (String key : data.keySet()) {
      String val = data.getString(key);
      if (key != null && val != null) {
        asMap.put(key, val);
      }
    }
    return new RemoteMessage(from, asMap);
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