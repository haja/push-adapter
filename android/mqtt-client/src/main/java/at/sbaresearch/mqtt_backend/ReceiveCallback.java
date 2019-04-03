package at.sbaresearch.mqtt_backend;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
    // TODO do actual json parsing instead of toString
    final Object raw = new JSONTokener(msg.toString()).nextValue();
    if (raw instanceof  String) {
      return new Payload("no id", (String) raw);
    }
    JSONObject obj = (JSONObject) raw;
    String id = obj.getString("id");
    String payload = obj.getString("payload");
    return new Payload(id, payload);
  }

  private void sendIntent(Payload payload) {
    // TODO register intent, well-defined intent constant
    Intent intent = new Intent(API.INTENT_MQTT_RECEIVE);
    // TODO raise intent with payload
    // TODO put data from mqtt backend here (appID? Identity hash? msg-content?)
    intent.putExtra(API.id, payload.id);
    intent.putExtra(API.payload, payload.payload);

    // TODO enforce some permission here? enforce package name here
    context.sendBroadcast(intent);
  }

  @Override
  public void deliveryComplete(IMqttDeliveryToken token) {
  }

  private class Payload {

    // TODO what fields are needed?
    private final String id;
    // TODO this should be a map? or binary?
    private final String payload;

    public Payload(String id, String payload) {
      this.id = id;
      this.payload = payload;
    }

    @Override
    public String toString() {
      final StringBuffer sb = new StringBuffer("Payload{");
      sb.append("id='").append(id).append('\'');
      sb.append(", payload='").append(payload).append('\'');
      sb.append('}');
      return sb.toString();
    }
  }
}
