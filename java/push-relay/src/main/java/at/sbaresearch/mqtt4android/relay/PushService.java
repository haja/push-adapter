package at.sbaresearch.mqtt4android.relay;

import at.sbaresearch.mqtt4android.registration.RegistrationStore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class PushService {

  RegistrationStore registrationStore;
  JmsTemplate jmsTemplate;

  public void pushMessage(String token, String msg) {
    log.info("pushing message: {}", msg);
    val topic = registrationStore.getTopic(token);
    jmsTemplate.convertAndSend(topic, msg);
  }

  public void pushToDummyTopic() {
    log.info("pushing to dummy topic");
    jmsTemplate.convertAndSend(MqttBrokerConfig.DUMMY_TOPIC, "foo");
  }
}
