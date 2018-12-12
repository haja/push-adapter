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
