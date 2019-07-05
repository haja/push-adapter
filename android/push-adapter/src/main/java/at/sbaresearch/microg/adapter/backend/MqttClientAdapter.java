package at.sbaresearch.microg.adapter.backend;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import at.sbaresearch.microg.adapter.backend.gms.gcm.GcmPrefs;
import at.sbaresearch.microg.adapter.backend.gms.gcm.GcmPrefs.MqttSettings;
import lombok.val;

import static at.sbaresearch.microg.adapter.backend.MQTT_API.*;
import static at.sbaresearch.microg.adapter.backend.gms.gcm.GcmConstants.PERMISSION_CONNECT;

public class MqttClientAdapter {

  private static final String TAG = MqttClientAdapter.class.getSimpleName();

  public static void ensureBackendConnection(Context ctx) {
    Log.d(TAG, "ensureBackendConnection");
    val prefs = GcmPrefs.get(ctx);
    val mqttSettings = prefs.getMqttSettings();

    val connectIntent = new Intent(MQTT_API.INTENT_MQTT_CONNECT);
    connectIntent.putExtras(fromSettings(mqttSettings));

    ctx.sendBroadcast(connectIntent, PERMISSION_CONNECT);
  }

  private static Bundle fromSettings(MqttSettings s) {
    val bundle = new Bundle(5);
    bundle.putString(INTENT_MQTT_CONNECT_HOST, s.getHost());
    bundle.putInt(INTENT_MQTT_CONNECT_PORT, s.getPort());
    bundle.putString(INTENT_MQTT_CONNECT_TOPIC, s.getTopic());
    bundle.putByteArray(INTENT_MQTT_CONNECT_CLIENT_KEY, s.getPrivKey());
    bundle.putByteArray(INTENT_MQTT_CONNECT_CLIENT_CERT, s.getCert());
    return bundle;
  }
}
