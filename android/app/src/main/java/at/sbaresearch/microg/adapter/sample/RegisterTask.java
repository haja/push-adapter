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

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import at.sbaresearch.microg.adapter.library.gms.iid.FirebaseInstanceId;
import at.sbaresearch.microg.adapter.library.gms.iid.FirebaseInstanceId.RelayConnection;
import at.sbaresearch.microg.adapter.sample.BackendRestClient.AppRegistrationRequest;
import lombok.AllArgsConstructor;
import lombok.val;
import retrofit2.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@AllArgsConstructor
public class RegisterTask extends AsyncTask<Context, Void, String> {

  private static final String TAG = RegisterTask.class.getSimpleName();
  private final BackendRestClient restClient;


  @Override
  protected String doInBackground(Context... ctx) {
    final FirebaseInstanceId instanceID = FirebaseInstanceId.getInstance(ctx[0]);
    try {
      final RelayConnection connection = instanceID.getToken("testId1", "FCM");
      Log.i(TAG, "doInBackground: registration successful, sending to backend");
      Log.d(TAG, "doInBackground: registration connection" + connection);

      Response<Void> response = restClient.sendRegistrationId(
          new AppRegistrationRequest(connection.token, connection.relayUrl, connection.cert)).execute();
      if (response.isSuccessful()){
        return connection.token;
      } else {
        Log.e(TAG, "doInBackground: rest call failed: " + response.message());
      }
    } catch (IOException e) {
      Log.e(TAG, "registration failed", e);
    }
    return null;
  }

  @Override
  protected void onPostExecute(String registrationId) {
    if (registrationId != null) {
      final String msg = "registration successful, registrationId: " + registrationId;
      Log.i(TAG, msg);
    }
  }
}
