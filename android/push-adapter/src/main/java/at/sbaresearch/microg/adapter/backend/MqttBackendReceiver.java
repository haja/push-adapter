package at.sbaresearch.microg.adapter.backend;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

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
    sendIntent(context, outgoingIntent);
  }

  private Intent generateIntent(Intent backendIntent) {
    // TODO sanitize intent?
    Intent out = new Intent(ACTION_C2DM_RECEIVE);

    // TODO lookup package and from of receiving app from appId of intent
    String id = backendIntent.getStringExtra(MQTT_API.id);
    out.putExtra(EXTRA_FROM, id);
    String clientPackageName = "at.sbaresearch.microg.adapter.sample";
    out.setPackage(clientPackageName);

    // TODO handle payload as bundle like GCM
    String payload = backendIntent.getStringExtra(MQTT_API.payload);
    out.putExtra("TODO-payload", payload);

    return out;
  }

  private void sendIntent(Context ctx, Intent outgoingIntent) {
    ctx.sendBroadcast(outgoingIntent);
  }
}
