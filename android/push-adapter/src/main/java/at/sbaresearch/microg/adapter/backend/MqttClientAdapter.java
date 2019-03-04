package at.sbaresearch.microg.adapter.backend;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MqttClientAdapter {

  private static final String TAG = MqttClientAdapter.class.getSimpleName();

  public static void ensureBackendConnection(Context ctx) {
    Log.d(TAG, "ensureBackendConnection");
    Intent connectIntent = new Intent(MQTT_API.INTENT_MQTT_CONNECT);
    ctx.sendBroadcast(connectIntent);
  }
}
