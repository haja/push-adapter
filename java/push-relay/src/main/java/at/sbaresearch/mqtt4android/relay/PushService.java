package at.sbaresearch.mqtt4android.relay;

import at.sbaresearch.mqtt4android.registration.RegistrationService;
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

  RegistrationService registrationService;
  JmsTemplate jmsTemplate;

  public void pushMessage(String token, String msg) {
    log.info("pushing message: {}", msg);
    val app = registrationService.getApp(token);
    // TODO properly build msg, include app and app_signature
    jmsTemplate.convertAndSend(app.getDeviceId().getId(), msg);
  }
}
