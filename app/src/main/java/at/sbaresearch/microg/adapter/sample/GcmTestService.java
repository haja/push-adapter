package at.sbaresearch.microg.adapter.sample;

import android.os.Bundle;
import android.util.Log;
import at.sbaresearch.microg.adapter.library.gms.gcm.GcmListenerService;

public class GcmTestService extends GcmListenerService {

  private static final String TAG = GcmTestService.class.getSimpleName();

  @Override
  public void onMessageReceived(String from, Bundle data) {
    final String msg = "### msg RECV: " + from + " data: " + data;
    //Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    Log.i(TAG, msg);
  }
}
