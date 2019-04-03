/*
 * Copyright (C) 2018 microG Project Team
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

package at.sbaresearch.microg.adapter.backend.gms.gcm;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.*;
import android.util.Log;
import at.sbaresearch.microg.adapter.backend.gms.checkin.LastCheckinInfo;
import at.sbaresearch.microg.adapter.backend.gms.common.PackageUtils;
import at.sbaresearch.microg.adapter.backend.gms.common.Utils;

import static at.sbaresearch.microg.adapter.backend.gms.gcm.GcmConstants.*;

class PushRegisterHandler extends Handler {
  private static final String TAG = "GmsGcmRegisterHdl";

  private Context context;
  private int callingUid;
  private GcmDatabase database;

  public PushRegisterHandler(Context context, GcmDatabase database) {
    this.context = context;
    this.database = database;
  }

  @Override
  public boolean sendMessageAtTime(Message msg, long uptimeMillis) {
    this.callingUid = Binder.getCallingUid();
    return super.sendMessageAtTime(msg, uptimeMillis);
  }

  private void sendReply(int what, int id, Messenger replyTo, Bundle data) {
    if (what == 0) {
      Intent outIntent = new Intent(ACTION_C2DM_REGISTRATION);
      outIntent.putExtras(data);
      Message message = Message.obtain();
      message.obj = outIntent;
      try {
        replyTo.send(message);
      } catch (RemoteException e) {
        Log.w(TAG, e);
      }
    } else {
      Bundle messageData = new Bundle();
      messageData.putBundle("data", data);
      Message response = Message.obtain();
      response.what = what;
      response.arg1 = id;
      response.setData(messageData);
      try {
        replyTo.send(response);
      } catch (RemoteException e) {
        Log.w(TAG, e);
      }
    }
  }

  private void replyError(int what, int id, Messenger replyTo, String errorMessage) {
    Bundle bundle = new Bundle();
    bundle.putString(EXTRA_ERROR, errorMessage);
    sendReply(what, id, replyTo, bundle);
  }

  private void replyNotAvailable(int what, int id, Messenger replyTo) {
    replyError(what, id, replyTo, ERROR_SERVICE_NOT_AVAILABLE);
  }

  @Override
  public void handleMessage(Message msg) {
    Log.d(TAG, "handleMessage " + msg);
    if (msg.what == 0) {
      if (msg.obj instanceof Intent) {
        Message nuMsg = Message.obtain();
        nuMsg.what = msg.what;
        nuMsg.arg1 = 0;
        nuMsg.replyTo = null;
        PendingIntent pendingIntent = ((Intent) msg.obj).getParcelableExtra(EXTRA_APP);
        String packageName = PackageUtils.packageFromPendingIntent(pendingIntent);
        Bundle data = new Bundle();
        data.putBoolean("oneWay", false);
        data.putString("pkg", packageName);
        data.putBundle("data", msg.getData());
        nuMsg.setData(data);
        msg = nuMsg;
      } else {
        return;
      }
    }

    int what = msg.what;
    int id = msg.arg1;
    Messenger replyTo = msg.replyTo;
    if (replyTo == null) {
      Log.w(TAG, "replyTo is null");
      return;
    }
    Bundle data = msg.getData();
    if (data.getBoolean("oneWay", false)) {
      Log.w(TAG, "oneWay requested");
      return;
    }

    String packageName = data.getString("pkg");
    Bundle subdata = data.getBundle("data");
    String sender = subdata.getString("sender");

    try {
      PackageUtils.checkPackageUid(context, packageName, callingUid);
    } catch (SecurityException e) {
      Log.w(TAG, e);
      return;
    }

    // TODO: We should checkin and/or ask for permission here.

    Log.d(TAG, "about to send register request");
    // TODO fix http requests
    PushRegisterManager.completeRegisterRequest(context, database,
        new RegisterRequest()
            .build(Utils.getBuild(context))
            .sender(sender)
            // TODO is checkin needed?
            .checkin(LastCheckinInfo.read(context))
            .app(packageName)
            .appid(subdata.getString("appid"), subdata.getString("gmp_app_id")),
        bundle -> sendReply(what, id, replyTo, bundle));
  }
}
