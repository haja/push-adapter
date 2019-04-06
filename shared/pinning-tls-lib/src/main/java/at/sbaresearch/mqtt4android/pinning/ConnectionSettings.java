package at.sbaresearch.mqtt4android.pinning;

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
