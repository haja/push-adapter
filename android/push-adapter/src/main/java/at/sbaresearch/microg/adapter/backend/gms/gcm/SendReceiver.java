/*
 * Copyright (C) 2013-2017, 2020 microG Project Team, Harald Jagenteufel
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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import static at.sbaresearch.microg.adapter.backend.gms.gcm.McsConstants.ACTION_SEND;

// TODO this is currently not used, see MqttBackendReceiver
public class SendReceiver extends WakefulBroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
    if (intent.getExtras() == null) return;
    Bundle extras = intent.getExtras();
    Log.d("GmsMcsSendRcvr", "original extras: " + extras);
    for (String key : extras.keySet()) {
      if (key.startsWith("GOOG.") || key.startsWith("GOOGLE.")) {
        extras.remove(key);
      }
    }
    Intent i = new Intent(context, McsService.class);
    i.setAction(ACTION_SEND);
    i.putExtras(extras);
    startWakefulService(context, i);
  }
}
