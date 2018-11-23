package at.sbaresearch.microg.adapter.backend.gms.checkin;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class CheckinService extends IntentService {
  private static final String TAG = "GmsCheckinSvc";
  public static final String BIND_ACTION = "com.google.android.gms.checkin.BIND_TO_SERVICE";
  public static final String EXTRA_FORCE_CHECKIN = "force";
  public static final String EXTRA_CALLBACK_INTENT = "callback";


  public CheckinService() {
    super(TAG);
  }

  @SuppressWarnings("MissingPermission")
  @Override
  protected void onHandleIntent(Intent intent) {
    // TODO checkin not implemented
    Log.w(TAG, "checkin not implemented");
  }
}
