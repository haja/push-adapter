package at.sbaresearch.mqtt_backend;

import android.os.Bundle;
import lombok.Value;
import static at.sbaresearch.mqtt_backend.API.*;


@Value
class ConnectionSettings {
  String host;
  int port;
  String topic;
  byte[] privKey;
  byte[] cert;

  String getServerUrl() {
    return "ssl://" + host + ":" + port;
  }

  static ConnectionSettings fromBundle(Bundle bundle) {
    return new ConnectionSettings(
        bundle.getString(INTENT_MQTT_CONNECT_HOST),
        bundle.getInt(INTENT_MQTT_CONNECT_PORT),
        bundle.getString(INTENT_MQTT_CONNECT_TOPIC),
        bundle.getByteArray(INTENT_MQTT_CONNECT_CLIENT_KEY),
        bundle.getByteArray(INTENT_MQTT_CONNECT_CLIENT_CERT)
    );
  }
}
