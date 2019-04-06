package at.sbaresearch.mqtt4android.registration;

import at.sbaresearch.mqtt4android.registration.crypto.ClientKeyFactory;
import at.sbaresearch.mqtt4android.registration.crypto.ClientKeyFactory.ClientKeys;
import at.sbaresearch.mqtt4android.relay.TopicRegistry;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.security.SecureRandom;
import java.util.Base64;

@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class DeviceService {

  private static int CLIENT_ID_LENGTH = 32;
  private static SecureRandom RNG = new SecureRandom();

  String mqttHostname;
  int mqttPort;
  ClientKeyFactory clientKeyFactory;
  TopicRegistry topicRegistry;

  public DeviceData registerDevice() throws Exception {
    val clientId = generateClientId();
    log.info("registering new device; clientId: {}", clientId);
    //  save clientCert ID / hash / whatever to associate with device
    val clientKeys = clientKeyFactory.createSignedKey(clientId);
    val topic = topicRegistry.createTopic(clientId);
    val mqttSettings = getMqttSettings();
    DeviceData deviceData = new DeviceData(clientKeys, topic, mqttSettings);
    log.info("device data generated: {}", deviceData);
    return deviceData;
  }

  private String generateClientId() {
    val idBytes = new byte[CLIENT_ID_LENGTH];
    RNG.nextBytes(idBytes);
    return Base64.getUrlEncoder().encodeToString(idBytes)
        .replaceAll("=", "");
  }

  private Tuple2<String, Integer> getMqttSettings() {
    return Tuple.of(mqttHostname, mqttPort);
  }


  @Value
  public static class DeviceData {
    ClientKeys clientKeys;
    String mqttTopic;
    Tuple2<String, Integer> mqttConnection;
  }

}
