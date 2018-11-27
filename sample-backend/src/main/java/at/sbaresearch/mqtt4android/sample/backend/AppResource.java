package at.sbaresearch.mqtt4android.sample.backend;

import io.vavr.collection.HashMap;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AppResource {

  private static final String URL_RELAY = "localhost:9876/push/{requestId}";

  @NonFinal
  Option<String> currentRegId = Option.none();
  RestTemplate restTemplate;

  public AppResource(RestTemplateBuilder builder) {
    this.restTemplate = builder.build();
  }

  @RequestMapping(value = "/register", method = RequestMethod.POST)
  public void register(String registrationId) {
    log.info("register call with registrationId {}", registrationId);
    // TODO link with app instance / userId.. mocked for now
    this.currentRegId = Option.of(registrationId);
  }

  @RequestMapping(value = "/send", method = RequestMethod.POST)
  public void sendMessage(String message) {
    currentRegId.peek(registrationId -> {
      val req = new PushRequest(message);
      restTemplate.postForLocation(URL_RELAY, req, HashMap.of("requestId", registrationId));
    }).onEmpty(() -> log.warn("not registered, cannot send message"));
  }

  @Value
  private class PushRequest {
    String message;
  }
}
