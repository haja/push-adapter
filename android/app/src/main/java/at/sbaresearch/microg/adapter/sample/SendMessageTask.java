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

import android.os.AsyncTask;
import android.util.Log;
import lombok.AllArgsConstructor;
import retrofit2.Response;

import java.io.IOException;

@AllArgsConstructor
public class SendMessageTask extends AsyncTask<String, Void, Void> {

  private final static String TAG = SendMessageTask.class.getSimpleName();
  private final BackendRestClient restClient;

  @Override
  protected Void doInBackground(String... msg) {
    try {
      Response<Void> response = restClient.sendMessage(msg[0]).execute();
      if (response.isSuccessful()){
        Log.d(TAG, "doInBackground: send rest call successful");
      } else {
        Log.e(TAG, "doInBackground: send rest call failed: " + response.message());
      }
    } catch (IOException e) {
      Log.e(TAG, "send failed", e);
    }
    return null;
  }
}
