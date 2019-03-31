package at.sbaresearch.mqtt4android.relay;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class PushService {

  JmsTemplate jmsTemplate;

  public void pushMessage(String msg) {
    log.info("pushing message: {}", msg);
    jmsTemplate.convertAndSend(MqttBrokerConfig.MQTT_MOCK_TOPIC, msg);
  }

  public void pushToDummyTopic() {
    log.info("pushing to dummy topic");
    jmsTemplate.convertAndSend(MqttBrokerConfig.DUMMY_TOPIC, "foo");
  }
}
