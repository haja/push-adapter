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

package at.sbaresearch.microg.adapter.backend.registration.app;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import at.sbaresearch.microg.adapter.backend.gms.common.PackageUtils;
import at.sbaresearch.microg.adapter.backend.gms.gcm.GcmDatabase;
import at.sbaresearch.microg.adapter.backend.gms.gcm.GcmPrefs;
import at.sbaresearch.microg.adapter.backend.gms.ui.AskPushPermission;

import static at.sbaresearch.microg.adapter.backend.gms.gcm.GcmConstants.*;

public class RegisterAppService extends IntentService {
  private static final String TAG = "RegisterAppSvc";
  private static final String EXTRA_SKIP_TRY_CHECKIN = "skip_checkin";

  private GcmDatabase database;
  private static boolean requestPending = false;
  private HttpRegisterAppService httpService = null;

  public RegisterAppService() {
    super(TAG);
    setIntentRedelivery(false);
  }

  @Override
  public void onCreate() {
    super.onCreate();
    database = new GcmDatabase(this);
    try {
      this.httpService = new HttpRegisterAppService(this);
    } catch (Exception e) {
      Log.e(TAG, "cannot create http service", e);
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    database.close();
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    WakefulBroadcastReceiver.completeWakefulIntent(intent);
    Log.d(TAG, "onHandleIntent: " + intent);

    String requestId = null;
    if (intent.hasExtra(EXTRA_KID) && intent.getStringExtra(EXTRA_KID).startsWith("|")) {
      String[] kid = intent.getStringExtra(EXTRA_KID).split("\\|");
      if (kid.length >= 3 && "ID".equals(kid[1])) {
        requestId = kid[2];
      }
    }

    // TODO checkin service call was here

    try {
      if (ACTION_C2DM_REGISTER.equals(intent.getAction())) {
        registerApp(intent, requestId);
      }
    } catch (Exception e) {
      Log.w(TAG, e);
    }
  }

  private void registerApp(final Intent intent, String requestId) {
    PendingIntent pendingIntent = intent.getParcelableExtra(EXTRA_APP);
    final String packageName = PackageUtils.packageFromPendingIntent(pendingIntent);

    GcmDatabase.App app = database.getApp(packageName);
    if (app == null && GcmPrefs.get(this).isConfirmNewApps()) {
      try {
        getPackageManager().getApplicationInfo(packageName, 0); // Check package exists
        Intent i = new Intent(this, AskPushPermission.class);
        i.putExtra(EXTRA_PENDING_INTENT, intent);
        i.putExtra(EXTRA_APP, packageName);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
      } catch (PackageManager.NameNotFoundException e) {
        replyNotAvailable(this, intent, packageName, requestId);
      }
    } else {
      registerAndReply(this, database, intent, packageName, requestId);
    }
  }

  public static void replyNotAvailable(Context context, Intent intent, String packageName,
      String requestId) {
    Intent outIntent = new Intent(ACTION_C2DM_REGISTRATION);
    outIntent.putExtra(EXTRA_ERROR,
        HttpRegisterAppService.attachRequestId(ERROR_SERVICE_NOT_AVAILABLE, requestId));
    sendReply(context, intent, packageName, outIntent);
  }

  public void registerAndReply(Context context, GcmDatabase database, Intent intent,
      String packageName, String requestId) {
    Log.d(TAG, "register[req]: " + intent.toString() + " extras=" + intent.getExtras());
    httpService.registerApp(context, database, packageName, intent.getStringExtra(EXTRA_SENDER),
        bundle -> {
          Intent outIntent = new Intent(ACTION_C2DM_REGISTRATION);
          outIntent.putExtras(bundle);
          Log.d(TAG, "register[res]: " + outIntent.toString() + " extras=" + outIntent.getExtras());
          // TODO is this needed?
          //MqttClientAdapter.ensureBackendConnection(context);
          sendReply(context, intent, packageName, outIntent);
        });
  }


  private static void sendReply(Context context, Intent intent, String packageName,
      Intent outIntent) {
    try {
      if (intent != null && intent.hasExtra(EXTRA_MESSENGER)) {
        Messenger messenger = intent.getParcelableExtra(EXTRA_MESSENGER);
        Message message = Message.obtain();
        message.obj = outIntent;
        messenger.send(message);
        return;
      }
    } catch (Exception e) {
      Log.w(TAG, e);
    }

    outIntent.setPackage(packageName);
    context.sendOrderedBroadcast(outIntent, null);
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    Log.d(TAG, "onBind: " + intent.toString());
    if (ACTION_C2DM_REGISTER.equals(intent.getAction())) {
      try {
        Messenger messenger = new Messenger(new RegisterAppHandler(this, database));
        return messenger.getBinder();
      } catch (Exception e) {
        Log.e(TAG, "cannot bind to messenger", e);
      }
    }
    return super.onBind(intent);
  }

}
