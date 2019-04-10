package at.sbaresearch.mqtt4android.relay;

import lombok.Value;

public class PushTestData {

  public static final RegistrationRecord registration1 = RegistrationRecord.of("registrationToken1", "testTopic1");

  @Value(staticConstructor = "of")
  public static class RegistrationRecord {
    String token;
    String topic;
  }
}
