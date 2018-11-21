package at.sbaresearch.mqtt_backend;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

class ReceiveCallback implements MqttCallback {
  private static final String TAG = "ReceiveCallback";
  private static final String INTENT_MQTT_RECEIVE =
      "at.sbaresearch.android.gcm.mqtt.intent.RECEIVE";

  private final Context context;

  public ReceiveCallback(Context ctx) {
    this.context = ctx;
  }

  @Override
  public void connectionLost(Throwable cause) {
    Log.e(TAG, "connection lost: " + cause.getMessage());
  }

  @Override
  public void messageArrived(String topic, MqttMessage message) throws Exception {
    Log.i(TAG, "MQTT msg recv: " + message.toString());
    Payload pay = parsePayload(message.getPayload());
    sendIntent(pay);
  }

  private Payload parsePayload(byte[] payload) {
    // TODO parse json payload
    return null;
  }

  private void sendIntent(Payload payload) {
    Intent intent = new Intent(INTENT_MQTT_RECEIVE);
    // TODO raise intent with payload
    // TODO put data from mqtt backend here (appID? Identity hash? msg-content?)
    // intent.putExtra(GCM_API.EXTRA_FROM, "testPushApp");

    context.sendBroadcast(intent);
  }

  @Override
  public void deliveryComplete(IMqttDeliveryToken token) {
  }

  private class Payload {
    // TODO what fields are needed?
  }
}
