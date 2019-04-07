package at.sbaresearch.mqtt4android.pinning;

import lombok.Value;

@Value
public class ClientKeyCert {
  byte[] privKey;
  byte[] cert;
}
