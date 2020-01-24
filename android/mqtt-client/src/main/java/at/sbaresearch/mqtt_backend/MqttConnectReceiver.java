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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MqttConnectReceiver extends BroadcastReceiver {

  private static final String TAG = MqttConnectReceiver.class.getSimpleName();

  @Override
  public void onReceive(Context context, Intent intent) {
    Log.i(TAG, "connect intent received");
    Intent out = new Intent(context, MqttConnectionManagerService.class);
    out.putExtras(intent);
    MqttConnectReceiverService.enqueueWork(context, out);
  }
}
