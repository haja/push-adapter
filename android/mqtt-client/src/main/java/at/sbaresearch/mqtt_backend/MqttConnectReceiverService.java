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

import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION_CODES;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;

public class MqttConnectReceiverService extends JobIntentService {
  public static final int JOB_ID = 3;

  public static void enqueueWork(Context context, Intent work) {
    enqueueWork(context, MqttConnectReceiverService.class, JOB_ID, work);
  }

  @Override
  protected void onHandleWork(@NonNull Intent intent) {
    if (android.os.Build.VERSION.SDK_INT >= VERSION_CODES.O) {
      getApplicationContext().startForegroundService(intent);
    } else {
      getApplicationContext().startService(intent);
    }
  }
}
