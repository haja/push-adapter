package at.sbaresearch.microg.adapter.backend.gms.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

public class RegisterDeviceService extends IntentService {
  private static final String TAG = "RegisterDeviceSvc";

  public RegisterDeviceService() {
    super(TAG);
  }

  @Override
  public void onCreate() {
    super.onCreate();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }

  @Override
  protected void onHandleIntent(@Nullable Intent intent) {
    Log.d(TAG, "onHandleIntent: " + intent);
    PushRegisterManager.registerDevice(this);
  }
}
