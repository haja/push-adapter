package at.sbaresearch.microg.adapter.backend;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import lombok.val;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static at.sbaresearch.microg.adapter.backend.gms.gcm.GcmConstants.ACTION_C2DM_RECEIVE;
import static at.sbaresearch.microg.adapter.backend.gms.gcm.GcmConstants.EXTRA_FROM;

/**
 * TODO should this service be a WakefulBroadcastReceiver, like in microG?
 */
public class MqttBackendReceiver extends BroadcastReceiver {

  private static final String TAG = MqttBackendReceiver.class.getSimpleName();

  @Override
  public void onReceive(Context context, Intent backendIntent) {
    Log.i(TAG, "onReceive: " + backendIntent);
    Intent outgoingIntent = generateIntent(backendIntent);
    Log.i(TAG, "onReceive: sending intent" + outgoingIntent);
    sendIntent(context, outgoingIntent);
  }

  private Intent generateIntent(Intent backendIntent) {
    // TODO sanitize intent?
    Intent out = new Intent(ACTION_C2DM_RECEIVE);

    // TODO lookup package and from of receiving app from appId of intent
    String app = backendIntent.getStringExtra(MQTT_API.app);
    // TODO FROM should be something else
    out.putExtra(EXTRA_FROM, app);
    String clientPackageName = app;
    out.setPackage(clientPackageName);

    String payload = backendIntent.getStringExtra(MQTT_API.payload);
    out.putExtra("payload", payload);
    String id = backendIntent.getStringExtra(MQTT_API.messageId);
    out.putExtra("messageId", id);

    return out;
  }

  private void sendIntent(Context ctx, Intent outgoingIntent) {
    ctx.sendBroadcast(outgoingIntent);
  }
}
