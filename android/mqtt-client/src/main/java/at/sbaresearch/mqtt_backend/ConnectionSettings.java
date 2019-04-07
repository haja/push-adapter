package at.sbaresearch.mqtt_backend;

import lombok.Value;

@Value
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
