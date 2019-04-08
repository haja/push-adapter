package at.sbaresearch.mqtt4android.relay;

import at.sbaresearch.mqtt4android.registration.RegistrationRepository;
import at.sbaresearch.mqtt4android.relay.mqtt.MqttBrokerConfig;
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

  // TODO use service here, not store/repo
  RegistrationRepository registrationRepository;
  JmsTemplate jmsTemplate;

  public void pushMessage(String token, String msg) {
    log.info("pushing message: {}", msg);
    val topic = registrationRepository.getTopic(token);
    jmsTemplate.convertAndSend(topic, msg);
  }

  public void pushToDummyTopic() {
    log.info("pushing to dummy topic");
    jmsTemplate.convertAndSend(MqttBrokerConfig.DUMMY_TOPIC, "foo");
  }
}
