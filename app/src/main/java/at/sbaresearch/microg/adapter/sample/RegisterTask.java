package at.sbaresearch.microg.adapter.sample;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import at.sbaresearch.microg.adapter.library.gms.gcm.GoogleCloudMessaging;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;

public class RegisterTask extends AsyncTask<Context, Void, String> {

  private final static String TAG = RegisterTask.class.getSimpleName();
  private static final String URL_BACKEND = "http://10.0.2.2:8888";
  private final BackendRestClient restClient;

  public RegisterTask() {
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(URL_BACKEND)
        .build();
    restClient = retrofit.create(BackendRestClient.class);
  }

  @Override
  protected String doInBackground(Context... ctx) {
    final GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(ctx[0]);
    try {
      final String id = gcm.register("testId1");
      Log.d(TAG, "doInBackground: registration successfull, sending to backend");

      Response<Void> response = restClient.sendRegistrationId(id).execute();
      if (response.isSuccessful()){
        return id;
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