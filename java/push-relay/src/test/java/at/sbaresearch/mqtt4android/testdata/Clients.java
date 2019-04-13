package at.sbaresearch.mqtt4android.testdata;

import at.sbaresearch.mqtt4android.registration.web.RegistrationResource.DeviceRegisterDto;
import at.sbaresearch.mqtt4android.registration.web.RegistrationResource.DeviceRegisterDto.DeviceRegisterDtoBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Profile("!test-setup")
@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PUBLIC)
@Getter
public class Clients {
  DeviceRegisterDtoBuilder client1;
  EncodedKeys client1Keys;
  DeviceRegisterDtoBuilder client2;
  EncodedKeys client2Keys;

  public Clients(
      @Value("${testSetup.ssl.clientKeysResource}") Resource keys,
      @Value("${mqtt.hostname}") String host,
      @Value("${mqtt.port}") int mqttPort,
      Registrations registrations
  ) throws IOException {

    client1Keys = loadKeys(keys, 1);
    client1 = defaultRegBuilder(host, mqttPort)
        .encodedPrivateKey(client1Keys.getPrivateKey())
        .encodedCert(client1Keys.getCert())
        .mqttTopic(registrations.registration1.getTopic());
    client2Keys = loadKeys(keys, 2);
    client2 = defaultRegBuilder(host, mqttPort)
        .encodedPrivateKey(client2Keys.getPrivateKey())
        .encodedCert(client2Keys.getCert())
        .mqttTopic(registrations.registration2.getTopic());
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
