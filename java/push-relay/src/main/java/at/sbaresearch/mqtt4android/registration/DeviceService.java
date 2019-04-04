package at.sbaresearch.mqtt4android.registration;

import at.sbaresearch.mqtt4android.registration.crypto.ClientKeyFactory;
import at.sbaresearch.mqtt4android.registration.crypto.ClientKeyFactory.ClientKeys;
import at.sbaresearch.mqtt4android.relay.MqttBrokerConfig;
import at.sbaresearch.mqtt4android.relay.TopicRegistry;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class DeviceService {

  String mqttHostname;
  int mqttPort;
  ClientKeyFactory clientKeyFactory;
  TopicRegistry topicRegistry;

  public DeviceData registerDevice() throws Exception {
    val clientId = "client";
    log.info("registering new device; clientId: {}", clientId);
    //  save clientCert ID / hash / whatever to associate with device
    val clientKeys = clientKeyFactory.createSignedKey(clientId);
    val topic = topicRegistry.createTopic(clientId);
    val mqttSettings = getMqttSettings();
    DeviceData deviceData = new DeviceData(clientKeys, topic, mqttSettings);
    log.info("device data generated: {}", deviceData);
    return deviceData;
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
