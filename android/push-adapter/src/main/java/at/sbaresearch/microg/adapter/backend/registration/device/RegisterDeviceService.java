package at.sbaresearch.microg.adapter.backend.registration.device;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

public class RegisterDeviceService extends IntentService {
  private static final String TAG = "RegisterDeviceSvc";

  private HttpRegisterDeviceService deviceService;

  public RegisterDeviceService() {
    super(TAG);
  }

  @Override
  public void onCreate() {
    super.onCreate();
    try {
      deviceService = new HttpRegisterDeviceService(this);
    } catch (Exception e) {
      Log.e(TAG, "creation of registerDeviceService failed", e);
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    // TODO cleanup needed?
    deviceService = null;
  }

  @Override
  protected void onHandleIntent(@Nullable Intent intent) {
    Log.d(TAG, "onHandleIntent: " + intent);
    if (deviceService != null) {
      deviceService.register(this);
    } else {
      Log.e(TAG, "cannot register, device service is null");
    }
  }
}
