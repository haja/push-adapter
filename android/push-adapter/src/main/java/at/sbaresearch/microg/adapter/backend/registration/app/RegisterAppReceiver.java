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

package at.sbaresearch.microg.adapter.backend.registration.app;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import static at.sbaresearch.microg.adapter.backend.gms.gcm.GcmConstants.ACTION_C2DM_REGISTER;

public class RegisterAppReceiver extends WakefulBroadcastReceiver {
  private static final String TAG = "RegAppReceiver";

  @Override
  public void onReceive(Context context, Intent intent) {
    Log.d(TAG, "onReceive: " + intent);
    Intent intent2 = new Intent(context, RegisterAppService.class);
    intent2.setAction(ACTION_C2DM_REGISTER);
    if (intent.getExtras() != null ) {
      intent2.putExtras(intent.getExtras());
    }
    startWakefulService(context, intent2);
  }
}
