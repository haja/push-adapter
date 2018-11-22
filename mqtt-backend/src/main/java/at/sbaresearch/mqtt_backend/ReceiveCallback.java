package at.sbaresearch.mqtt_backend;

import android.content.Context;
import android.content.Intent;
import android.util.JsonReader;
import android.util.Log;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.Reader;

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
    Payload pay = parsePayload(message);
    sendIntent(pay);
  }

  private Payload parsePayload(MqttMessage msg) throws JSONException {
    // TODO do actual json parsing instead of toString
    JSONObject obj = (JSONObject) new JSONTokener(msg.toString()).nextValue();
    String id = obj.getString("id");
    String payload = obj.getString("payload");
    return new Payload(id, payload);
  }

  private void sendIntent(Payload payload) {
    // TODO register intent, well-dfine intent constant
    Intent intent = new Intent(INTENT_MQTT_RECEIVE);
    // TODO raise intent with payload
    // TODO put data from mqtt backend here (appID? Identity hash? msg-content?)
    // intent.putExtra(GCM_API.EXTRA_FROM, "testPushApp");
    intent.putExtra(API.id, payload.id);
    intent.putExtra(API.payload, payload.payload);

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
  }
}
