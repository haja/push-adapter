package at.sbaresearch.microg.adapter.backend;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * TODO is this service needed?
 */
public class BackendReceiverService extends Service {

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }
}
