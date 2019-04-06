package at.sbaresearch.mqtt4android.relay.web;

import at.sbaresearch.mqtt4android.relay.PushService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
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

  @RequestMapping(value = "/{registrationId}", method = RequestMethod.POST)
  public void sendMessage(@PathVariable String registrationId, @RequestBody String message) {
    log.info("*** push message {} for registrationId {} received", message, registrationId);

    pushService.pushMessage(registrationId, message);
  }
}
