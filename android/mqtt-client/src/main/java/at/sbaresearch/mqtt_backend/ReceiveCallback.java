package at.sbaresearch.mqtt_backend;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import lombok.Value;
import lombok.val;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

class ReceiveCallback implements MqttCallback {
  private static final String TAG = "ReceiveCallback";

  private final Context context;
  private final int id;

  public ReceiveCallback(Context ctx, int id) {
    this.id = id;
    this.context = ctx;
  }

  @Override
  public void connectionLost(Throwable cause) {
    if (cause != null) {
      Log.e(TAG, id + " connection lost: " + cause.getMessage());
    } else {
      Log.w(TAG, id +  " connection lost: without cause (client disconnected?)");
    }
  }

  @Override
  public void messageArrived(String topic, MqttMessage message) {
    Log.i(TAG, id + " MQTT msg recv: " + message.toString());
    try {
      Message pay = parsePayload(message);
      Log.i(TAG, "MQTT msg parsed: " + pay.toString());
      sendIntent(pay);
    } catch (JSONException e) {
      Log.e(TAG, "MQTT msg parsing failed", e);
    }
  }

  private Message parsePayload(MqttMessage msg) throws JSONException {
    final JSONObject json = (JSONObject) new JSONTokener(new String(msg.getPayload())).nextValue();
    String app = json.getString("app");
    String sig = json.getString("signature");
    String name = json.getString("messageId");
    val data = json.getJSONObject("data").toString();
    long sentTime = json.getLong("sentTime");
    return new Message(app, sig, name, data, sentTime);
  }

  private void sendIntent(Message message) {
    // TODO register intent, well-defined intent constant
    Intent intent = new Intent(API.INTENT_MQTT_RECEIVE);
    intent.putExtra(API.app, message.app);
    intent.putExtra(API.signature, message.signature);
    intent.putExtra(API.messageId, message.messageId);
    intent.putExtra(API.payload, message.dataAsJson);
    intent.putExtra(API.sentTime, message.sentTime);

    // TODO enforce some permission here? enforce package messageId here
    context.sendBroadcast(intent);
  }

  @Override
  public void deliveryComplete(IMqttDeliveryToken token) {
    // nop
  }

  @Value
  private class Message {
    String app;
    String signature;
    String messageId;
    String dataAsJson;
    long sentTime;
  }
}
