package at.sbaresearch.mqtt4android.registration;

import at.sbaresearch.mqtt4android.relay.MqttBrokerConfig;
import at.sbaresearch.mqtt4android.relay.PushService;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.activemq.command.ActiveMQTopic;

@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class DeviceService {

  String mqttHostname;
  PushService pushService;

  public DeviceData registerDevice() {
    val cert = createClientCert();
    //  save clientCert ID / hash / whatever to associate with device
    val topic = createTopic(cert);
    val mqttSettings = getMqttSettings();
    return new DeviceData(cert, topic, mqttSettings);
  }

  private String createTopic(ClientCert cert) {
    val topicName = "foo";
    ActiveMQTopic topic = new ActiveMQTopic(topicName);

    // TODO create mqtt topic and authorize clientCert for this topic
    //  for now, this creates the mocked topic
    pushService.pushMessage("hello");

    //  return clientCert + connection settings + topic
    return topicName;
  }

  private Tuple2<String, String> getMqttSettings() {
    return Tuple.of(mqttHostname, MqttBrokerConfig.mqttPort);
  }

  private ClientCert createClientCert() {
    // TODO create clientCert
    return new ClientCert();
  }

  @Value
  public static class DeviceData {
    ClientCert clientCert;
    String mqttTopic;
    Tuple2 mqttConnection;
  }

  private static class ClientCert {
  }
}
