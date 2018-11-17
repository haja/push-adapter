package at.sbaresearch.microgadapter;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import at.sbaresearch.microgadapterlibrary.gms.gcm.GcmListenerService;

public class GcmTestService extends GcmListenerService {

  private static final String TAG = GcmTestService.class.getSimpleName();

  @Override
  public void onMessageReceived(String from, Bundle data) {
    final String msg = "msg recv: " + from + " data: " + data;
    //Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    Log.i(TAG, msg);
  }
}
