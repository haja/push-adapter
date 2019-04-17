package at.sbaresearch.mqtt4android.relay.web;

import at.sbaresearch.mqtt4android.relay.PushService;
import at.sbaresearch.mqtt4android.relay.PushService.PushMessage;
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

  @PostMapping("/")
  public PushResponse sendMessage(@RequestBody PushRequest req) {
    val msg = req.getMessage();
    log.info("push message {} received", msg);
    val token = msg.getToken();

    return toResponse(pushService.pushMessage(token, toMessage(msg)));
  }

  private PushResponse toResponse(String pushMessage) {
    return new PushResponse("projects/custom_relay/messages/" + pushMessage);
  }

  private PushMessage toMessage(PushRequest.Message msg) {
    return PushMessage.of(msg.getData());
  }

}
