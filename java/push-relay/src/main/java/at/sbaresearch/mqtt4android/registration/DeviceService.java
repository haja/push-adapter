package at.sbaresearch.mqtt4android.registration;

import at.sbaresearch.mqtt4android.registration.crypto.ClientKeyFactory;
import at.sbaresearch.mqtt4android.registration.crypto.ClientKeyFactory.ClientKeys;
import at.sbaresearch.mqtt4android.relay.MqttBrokerConfig;
import at.sbaresearch.mqtt4android.relay.PushService;
import at.sbaresearch.mqtt4android.relay.TopicRegistry;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class DeviceService {

  String mqttHostname;
  PushService pushService;
  ClientKeyFactory clientKeyFactory;
  TopicRegistry topicRegistry;

  public DeviceData registerDevice() throws Exception {
    val clientId = "client";
    log.info("registering new device; clientId: {}", clientId);
    //  save clientCert ID / hash / whatever to associate with device
    val clientKeys = clientKeyFactory.createSignedKey(clientId);
    val topic = topicRegistry.createTopic(clientKeys);
    val mqttSettings = getMqttSettings();
    return new DeviceData(clientKeys, topic, mqttSettings);
  }

  private Tuple2<String, String> getMqttSettings() {
    return Tuple.of(mqttHostname, MqttBrokerConfig.mqttPort);
  }


  @Value
  public static class DeviceData {
    ClientKeys clientKeys;
    String mqttTopic;
    Tuple2 mqttConnection;
  }

}
