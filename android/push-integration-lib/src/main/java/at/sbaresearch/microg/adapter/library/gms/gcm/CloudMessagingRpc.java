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

package at.sbaresearch.microg.adapter.library.gms.gcm;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.*;
import android.util.Log;
import at.sbaresearch.microg.adapter.library.gms.iid.FirebaseInstanceId;
import at.sbaresearch.microg.adapter.library.gms.iid.FirebaseInstanceId.RelayConnection;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static at.sbaresearch.microg.adapter.library.gms.common.Constants.GMS_PACKAGE_NAME;
import static at.sbaresearch.microg.adapter.library.gms.common.Constants.GSF_PACKAGE_NAME;
import static at.sbaresearch.microg.adapter.library.gms.gcm.GcmConstants.*;

public class CloudMessagingRpc {
  /**
   * The device can't read the response, or there was a 500/503 from the
   * server that can be retried later. The application should use exponential
   * back off and retry.
   */
  public static final String ERROR_SERVICE_NOT_AVAILABLE = GcmConstants.ERROR_SERVICE_NOT_AVAILABLE;
  public static final String TAG = "GoogleCloudMessagingRpc";

  private static final AtomicInteger messageIdCounter = new AtomicInteger(1);
  private static String gcmPackageName;

  private final BlockingQueue<Intent> messengerResponseQueue = new LinkedBlockingQueue<Intent>();
  private final Messenger messenger = new Messenger(new Handler(Looper.getMainLooper()) {
    @Override
    public void handleMessage(Message msg) {
      if (msg == null || !(msg.obj instanceof Intent)) {
        // Invalid message -> drop
        return;
      }
      Intent intent = (Intent) msg.obj;
      if (ACTION_C2DM_REGISTRATION.equals(intent.getAction())) {
        messengerResponseQueue.add(intent);
      }
    }
  });

  /**
   * Due to it's nature of being a monitored reference, Intents can be used to authenticate a package source.
   */
  private PendingIntent selfAuthIntent;
  private Context context;

  public CloudMessagingRpc(Context context) {
    this.context = context;
  }

  public static String getGcmPackageName(Context context) {
    Log.i(TAG, "getGcmPackageName");
    if (gcmPackageName != null) {
      return gcmPackageName;
    }
    PackageManager packageManager = context.getPackageManager();
    for (ResolveInfo resolveInfo : packageManager
        .queryIntentServices(new Intent(ACTION_C2DM_REGISTER), 0)) {
      if (packageManager.checkPermission(PERMISSION_RECEIVE, resolveInfo.serviceInfo.packageName) ==
          PERMISSION_GRANTED) {
        return gcmPackageName = resolveInfo.serviceInfo.packageName;
      }
    }
    try {
      ApplicationInfo appInfo = packageManager.getApplicationInfo(GMS_PACKAGE_NAME, 0);
      return gcmPackageName = appInfo.packageName;
    } catch (PackageManager.NameNotFoundException ignored) {
    }
    try {
      ApplicationInfo appInfo = packageManager.getApplicationInfo(GSF_PACKAGE_NAME, 0);
      return gcmPackageName = appInfo.packageName;
    } catch (PackageManager.NameNotFoundException ex3) {
      return null;
    }
  }

  public void close() {
    // Cancel the authentication
    if (selfAuthIntent != null) {
      selfAuthIntent.cancel();
      selfAuthIntent = null;
    }
  }

  private PendingIntent getSelfAuthIntent() {
    if (selfAuthIntent == null) {
      Intent intent = new Intent();
      intent.setPackage("com.google.example.invalidpackage");
      selfAuthIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
    }
    return selfAuthIntent;
  }

  public Intent sendRegisterMessageBlocking(Bundle extras) throws IOException {
    sendRegisterMessage(extras);
    Intent resultIntent;
    try {
      resultIntent = messengerResponseQueue.poll(30, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      throw new IOException(e.getMessage());
    }
    if (resultIntent == null) {
      throw new IOException(ERROR_SERVICE_NOT_AVAILABLE);
    }
    return resultIntent;
  }

  private void sendRegisterMessage(Bundle extras) {
    Intent intent = new Intent(ACTION_C2DM_REGISTER);
    final String gcmPackageName = getGcmPackageName(context);
    Log.i(TAG, "got package name " + gcmPackageName);
    intent.setPackage(gcmPackageName);
    extras.putString(EXTRA_MESSAGE_ID, "google.rpc" + messageIdCounter.getAndIncrement());
    intent.putExtras(extras);
    intent.putExtra(EXTRA_MESSENGER, messenger);
    intent.putExtra(EXTRA_APP, getSelfAuthIntent());
    context.startService(intent);
  }

  public RelayConnection handleRegisterMessageResult(Intent resultIntent) throws IOException {
    if (resultIntent == null) throw new IOException(FirebaseInstanceId.ERROR_SERVICE_NOT_AVAILABLE);
    String token = resultIntent.getStringExtra(EXTRA_REGISTRATION_ID);
    String host = resultIntent.getStringExtra(EXTRA_RELAY_HOST);
    byte[] cert = resultIntent.getByteArrayExtra(EXTRA_RELAY_CERT);
    if (token != null && host != null && cert != null) {
      return new RelayConnection(token, host, cert);
    }
    token = resultIntent.getStringExtra(EXTRA_ERROR);
    throw new IOException(token != null ? token : FirebaseInstanceId.ERROR_SERVICE_NOT_AVAILABLE);
  }
}
