package at.sbaresearch.mqtt_backend;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import lombok.Value;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

class ReceiveCallback implements MqttCallback {
  private static final String TAG = "ReceiveCallback";

  private final Context context;

  public ReceiveCallback(Context ctx) {
    this.context = ctx;
  }

  @Override
  public void connectionLost(Throwable cause) {
    Log.e(TAG, "connection lost: " + (cause != null ? cause.getMessage() : ""));
  }

  @Override
  public void messageArrived(String topic, MqttMessage message) {
    Log.i(TAG, "MQTT msg recv: " + message.toString());
    try {
      Payload pay = parsePayload(message);
      Log.i(TAG, "MQTT msg parsed: " + pay.toString());
      sendIntent(pay);
    } catch (JSONException e) {
      Log.e(TAG, "MQTT msg parsing failed", e);
    }
  }

  private Payload parsePayload(MqttMessage msg) throws JSONException {
    final JSONObject json = (JSONObject) new JSONTokener(new String(msg.getPayload())).nextValue();
    String app = json.getString("app");
    String sig = json.getString("signature");
    String message = json.getString("message");
    return new Payload(app, sig, message);
  }

  private void sendIntent(Payload payload) {
    // TODO register intent, well-defined intent constant
    Intent intent = new Intent(API.INTENT_MQTT_RECEIVE);
    intent.putExtra(API.app, payload.app);
    intent.putExtra(API.signature, payload.signature);
    intent.putExtra(API.payload, payload.payload);

    // TODO enforce some permission here? enforce package name here
    context.sendBroadcast(intent);
  }

  @Override
  public void deliveryComplete(IMqttDeliveryToken token) {
    // nop
  }

  @Value
  private class Payload {
    String app;
    String signature;
    // TODO this should be a map? or binary?
    String payload;
  }
}
