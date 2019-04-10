package at.sbaresearch.mqtt4android;

import at.sbaresearch.mqtt4android.registration.web.RegistrationResource.DeviceRegisterDto;
import at.sbaresearch.mqtt4android.registration.web.RegistrationResource.DeviceRegisterDto.DeviceRegisterDtoBuilder;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PUBLIC)
public class TestData {

  Clients clients;
  Registrations registrations;

  public TestData(
      @Value("${testSetup.ssl.clientKeysResource}")
          Resource clientKeysPath,
      @Value("${mqtt.hostname}") String host,
      @Value("${mqtt.port}") int mqttPort
      )
      throws IOException {
    this.registrations = new Registrations();
    this.clients = new Clients(host, mqttPort, clientKeysPath, registrations);
  }

  @FieldDefaults(makeFinal = true, level = AccessLevel.PUBLIC)
  public static class Clients {
    DeviceRegisterDtoBuilder client1;

    public Clients(String host, int mqttPort, Resource keys, Registrations registrations) throws IOException {
      val regBase = defaultRegBuilder(host, mqttPort);

      val client1Keys = loadKeys(keys, 1);
      client1 = regBase
          .encodedPrivateKey(client1Keys.getPrivateKey())
          .encodedCert(client1Keys.getCert())
          .mqttTopic(registrations.registration1.topic);
    }

    private DeviceRegisterDtoBuilder defaultRegBuilder(String host, int mqttPort) {
      return DeviceRegisterDto.testWith()
          .host(host)
          .port(mqttPort);
    }

    private EncodedKeys loadKeys(Resource keys, int id) throws IOException {
      val key = keys.createRelative("key-" + id);
      val cert = keys.createRelative("cert-" + id);

      val keyBytes = key.getInputStream().readAllBytes();
      val certBytes = cert.getInputStream().readAllBytes();

      return new EncodedKeys(keyBytes, certBytes);
    }

    @lombok.Value
    public static class EncodedKeys {
      byte[] privateKey;
      byte[] cert;
    }
  }

  public static class Registrations {
    public final RegistrationRecord registration1 =
        RegistrationRecord.of("registrationToken1", "testTopic1");

    @lombok.Value(staticConstructor = "of")
    public static class RegistrationRecord {
      String token;
      String topic;
    }
  }
}
