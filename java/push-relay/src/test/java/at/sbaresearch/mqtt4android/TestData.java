package at.sbaresearch.mqtt4android;

import at.sbaresearch.mqtt4android.registration.crypto.ClientKeyFactory.ClientKeys;
import lombok.AccessLevel;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PUBLIC)
public class TestData {

  Clients clients;
  Registrations registrations;

  public TestData() {
    this.clients = new Clients();
    this.registrations = new Registrations();
  }

  @FieldDefaults(makeFinal = true)
  public static class Clients {

    EncodedKeys client1;

    public Clients() {
      this.client1 = loadKeys(1);
    }

    private EncodedKeys loadKeys(int i) {
      return null;
    }

    @Value
    public static class EncodedKeys {
      byte[] privateKey;
      byte[] cert;
    }
  }

  public static class Registrations {
    public final RegistrationRecord registration1 =
        RegistrationRecord.of("registrationToken1", "testTopic1");

    @Value(staticConstructor = "of")
    public static class RegistrationRecord {
      String token;
      String topic;
    }
  }
}
