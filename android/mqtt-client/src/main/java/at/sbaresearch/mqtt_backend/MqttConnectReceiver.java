package at.sbaresearch.mqtt_backend;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MqttConnectReceiver extends BroadcastReceiver {

  private static final String TAG = MqttConnectReceiver.class.getSimpleName();

  @Override
  public void onReceive(Context context, Intent intent) {
    Log.i(TAG, "connect intent received");
    Intent out = new Intent(context, MqttConnectionManagerService.class);
    out.putExtras(intent);
    context.startService(out);
  }
}
