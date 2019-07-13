package at.sbaresearch.mqtt_backend;

public class API {
  public static final String INTENT_MQTT_RECEIVE =
      "at.sbaresearch.android.gcm.mqtt.intent.RECEIVE";

  public static final String INTENT_MQTT_CONNECT_HOST = "host";
  public static final String INTENT_MQTT_CONNECT_PORT = "port";
  public static final String INTENT_MQTT_CONNECT_TOPIC = "topic";
  public static final String INTENT_MQTT_CONNECT_CLIENT_KEY = "clientkey";
  public static final String INTENT_MQTT_CONNECT_CLIENT_CERT = "clientcert";

  public static final String SEND_PERM = "at.sbaresearch.android.gcm.mqtt.intent.SEND";
  public static final String CONNECT_PERM = "at.sbaresearch.android.c2dm.permission.CONNECT";

  public static final String ADAPTER_PKG = "at.sbaresearch.microg.adapter.backend";
  public static final String ADAPTER_MSG_RECEIVER = "at.sbaresearch.microg.adapter.backend.MqttBackendReceiver";

  public static final String app = "app";
  public static final String signature = "sig";
  public static final String payload = "payload";
  public static final String messageId = "messageId";
  public static final String sentTime = "sent_time";
  public static final String senderId = "senderId";
}
