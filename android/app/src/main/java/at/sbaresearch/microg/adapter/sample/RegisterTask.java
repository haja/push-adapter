package at.sbaresearch.microg.adapter.sample;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import at.sbaresearch.microg.adapter.library.gms.iid.FirebaseInstanceId;
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
      final String id = instanceID.getToken("testId1", "FCM");
      Log.i(TAG, "doInBackground: registration successful, sending to backend");
      Log.d(TAG, "doInBackground: registration id" + id);

      byte[] cert = readCert(ctx[0]);

      Response<Void> response = restClient.sendRegistrationId(
          new AppRegistrationRequest(id, cert)).execute();
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

  private byte[] readCert(Context ctx) throws IOException {
    val certStream = ctx.getResources().openRawResource(R.raw.server);
    int b;
    val bos = new ByteArrayOutputStream();
    while((b = certStream.read()) != -1) {
      bos.write(b);
    }
    return bos.toByteArray();
  }

  @Override
  protected void onPostExecute(String registrationId) {
    if (registrationId != null) {
      final String msg = "registration successful, registrationId: " + registrationId;
      Log.i(TAG, msg);
    }
  }
}
