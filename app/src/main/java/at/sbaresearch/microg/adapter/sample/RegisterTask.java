package at.sbaresearch.microg.adapter.sample;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import at.sbaresearch.microg.adapter.library.gms.gcm.GoogleCloudMessaging;
import at.sbaresearch.microg.adapter.sample.BackendRestClient.AppRegistrationRequest;
import lombok.val;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;

public class RegisterTask extends AsyncTask<Context, Void, String> {

  private final static String TAG = RegisterTask.class.getSimpleName();
  private static final String URL_BACKEND = "http://10.0.2.2:8888";
  private final BackendRestClient restClient;

  public RegisterTask() {
    val logger = new HttpLoggingInterceptor();
    logger.setLevel(Level.BODY);
    val cl = new OkHttpClient.Builder();
    cl.addInterceptor(logger);
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(URL_BACKEND)
        .addConverterFactory(JacksonConverterFactory.create())
        .client(cl.build())
        .build();
    restClient = retrofit.create(BackendRestClient.class);
  }

  @Override
  protected String doInBackground(Context... ctx) {
    final GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(ctx[0]);
    try {
      final String id = gcm.register("testId1");
      Log.i(TAG, "doInBackground: registration successful, sending to backend");
      Log.d(TAG, "doInBackground: registration id" + id);

      Response<Void> response = restClient.sendRegistrationId(
          new AppRegistrationRequest(id)).execute();
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
