/*
 * Copyright (c) 2020 Harald Jagenteufel.
 *
 * This file is part of push-relay.
 *
 *     push-relay is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     push-relay is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with push-relay.  If not, see <https://www.gnu.org/licenses/>.
 */

package at.sbaresearch.microg.adapter.sample;

import android.util.Log;
import android.widget.Toast;
import at.sbaresearch.microg.adapter.library.firebase.messaging.FirebaseMessagingService;
import at.sbaresearch.microg.adapter.library.firebase.messaging.RemoteMessage;

public class GcmTestService extends FirebaseMessagingService {

  private static final String TAG = GcmTestService.class.getSimpleName();

  @Override
  public void onMessageReceived(RemoteMessage message) {
    final String msg = "### msg RECV: " + message.getFrom() + " data: " + message.getData();
    //Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    Log.i(TAG, msg);
  }
}
