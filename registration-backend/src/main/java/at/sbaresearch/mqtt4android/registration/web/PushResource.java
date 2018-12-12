package at.sbaresearch.mqtt4android.registration.web;

import at.sbaresearch.mqtt4android.registration.PushService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/push")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
@AllArgsConstructor
public class PushResource {

  PushService pushService;

  @RequestMapping(value = "/{registrationId}", method = RequestMethod.POST)
  public void sendMessage(@PathVariable String registrationId, @RequestBody String message) {
    log.info("*** push message {} for registrationId {} received", message, registrationId);

    // TODO get device
    pushService.pushMessage(message);
  }
}
