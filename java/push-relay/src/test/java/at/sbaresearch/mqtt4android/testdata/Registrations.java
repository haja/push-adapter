package at.sbaresearch.mqtt4android.testdata;

import lombok.AccessLevel;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PUBLIC)
public class Registrations {
  RegistrationRecord registration1 =
      RegistrationRecord.of("registrationToken1", "device1");
  RegistrationRecord registration2 =
      RegistrationRecord.of("registrationToken2", "device2");

  @Value(staticConstructor = "of")
  public static class RegistrationRecord {
    String token;
    String topic;
  }
}
