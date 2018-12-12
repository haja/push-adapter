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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AppResource {

  private static final String URL_RELAY = "http://localhost:9876/push/{requestId}";

  @NonFinal
  Option<String> currentRegId = Option.none();
  RestTemplate restTemplate;

  public AppResource(RestTemplateBuilder builder) {
    this.restTemplate = builder.build();
  }

  @RequestMapping(value = "/register", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public void register(@RequestBody AppRegistrationRequestLombok request) {
    log.info("register called with registrationId {}", request.getRegistrationId());
    // TODO link with app instance / userId.. mocked for now
    this.currentRegId = Option.of(request.getRegistrationId());
  }

  @RequestMapping(value = "/send", method = RequestMethod.POST)
  public void sendMessage(@RequestBody String message) {
    log.info("sending message: {}", message);
    currentRegId.peek(registrationId -> {
      restTemplate.postForLocation(URL_RELAY, message, HashMap.of("requestId", registrationId).toJavaMap());
    }).onEmpty(() -> log.warn("not registered, cannot send message"));
  }

  @Value
  public static class AppRegistrationRequestLombok {
    String registrationId;
  }
}
