package at.sbaresearch.push_notifier;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * TODO should this service be a WakefulBroadcastReceiver, like in microG?
 */
public class MqttBackendReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent backendIntent) {
    Intent outgoingIntent = generateIntent(backendIntent);
    sendIntent(context, outgoingIntent);
  }

  private Intent generateIntent(Intent backendIntent) {
    // TODO sanitize intent?
    Intent out = new Intent(GCM_API.INTENT_RECEIVE);

    // TODO lookup package and from of receiving app from appId of intent
    String id = backendIntent.getStringExtra(MQTT_API.id);
    out.putExtra(GCM_API.EXTRA_FROM, id);
    String clientPackageName = "at.sbaresearch.microgadapter";
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
