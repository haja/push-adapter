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
