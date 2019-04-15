package at.sbaresearch.mqtt_backend;

import lombok.ToString;
import lombok.Value;

@Value
@ToString(exclude = {"privKey", "cert"})
public class ConnectionSettings {
  String host;
  int port;
  String topic;
  byte[] privKey;
  byte[] cert;

  public String getServerUrl() {
    return "ssl://" + host + ":" + port;
  }

}
