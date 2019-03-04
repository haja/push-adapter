package at.sbaresearch.microg.adapter.backend;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootBroadcastReceiver extends BroadcastReceiver {
  private static final String TAG = BootBroadcastReceiver.class.getSimpleName();

  @Override
  public void onReceive(Context ctx, Intent intent) {
    Log.d(TAG, "boot complete");
    MqttClientAdapter.ensureBackendConnection(ctx);
  }
}
