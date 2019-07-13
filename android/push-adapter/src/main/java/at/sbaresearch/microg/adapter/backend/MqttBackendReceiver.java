package at.sbaresearch.microg.adapter.backend;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static at.sbaresearch.microg.adapter.backend.gms.gcm.GcmConstants.*;

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
    //  still needed?
    String senderId = backendIntent.getStringExtra(MQTT_API.senderId);
    out.putExtra(EXTRA_FROM, senderId);
    String clientPackageName = backendIntent.getStringExtra(MQTT_API.app);
    out.setPackage(clientPackageName);

    long sentTime = backendIntent.getLongExtra(MQTT_API.sentTime, 0L);
    out.putExtra(EXTRA_SENT_TIME, sentTime);

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
