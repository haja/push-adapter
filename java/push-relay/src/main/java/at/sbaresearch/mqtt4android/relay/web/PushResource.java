package at.sbaresearch.mqtt4android.relay.web;

import at.sbaresearch.mqtt4android.relay.PushService;
import at.sbaresearch.mqtt4android.relay.PushService.PushMessage;
import io.vavr.collection.Map;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(PushResource.PUSH)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
@AllArgsConstructor
public class PushResource {

  public static final String PUSH = "/push";

  PushService pushService;

  // TODO map from FCM format here
  @PostMapping("/")
  public void sendMessage(@RequestBody PushDto msg) {
    log.info("push message {} for token {} received", msg.name, msg.token);
    val token = msg.token;

    pushService.pushMessage(token, toMessage(msg));
  }

  private PushMessage toMessage(PushDto msg) {
    return PushMessage.of(msg.name, msg.data);
  }

  /**
   * TODO this should be FCM/relay compatible API definition
   * see for proper definition
   * https://firebase.google.com/docs/reference/fcm/rest/v1/projects.messages/send
   */
  @Value
  @Builder
  public static class PushDto {
    // TODO wrap this in "message" object?
    // TODO remove name, is "output only" identifier in FCM
    String name;
    Map<String, String> data;
    String token;
  }
}
