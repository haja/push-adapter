package at.sbaresearch.microg.adapter.backend;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.util.Log;

public class BootBroadcastReceiverService extends JobIntentService {
  private static final String TAG = "BootRecvSrv";

  public static final int JOB_ID = 1;

  public static void enqueueWork(Context context, Intent work) {
    enqueueWork(context, BootBroadcastReceiverService.class, JOB_ID, work);
  }

  @Override
  protected void onHandleWork(@NonNull Intent intent) {
    Log.d(TAG, "boot complete: handle work");
    MqttClientAdapter.ensureBackendConnection(getApplicationContext());
  }
}
