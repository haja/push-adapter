package at.sbaresearch.mqtt4android;

import lombok.AccessLevel;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PUBLIC)
public class TestData {

  Clients clients;
  Registrations registrations;

  public TestData(
      @org.springframework.beans.factory.annotation.Value("${testSetup.ssl.clientKeysResource}")
          Resource clientKeysPath)
      throws IOException {
    this.clients = new Clients(clientKeysPath);
    this.registrations = new Registrations();
  }

  @FieldDefaults(makeFinal = true)
  public static class Clients {

    EncodedKeys client1;
    private Resource keys;

    public Clients(Resource keys) throws IOException {
      this.keys = keys;
      this.client1 = loadKeys(1);
    }

    private EncodedKeys loadKeys(int id) throws IOException {
      val key = keys.createRelative("key-" + id);
      val cert = keys.createRelative("cert-" + id);

      val keyBytes = key.getInputStream().readAllBytes();
      val certBytes = cert.getInputStream().readAllBytes();

      return new EncodedKeys(keyBytes, certBytes);
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
