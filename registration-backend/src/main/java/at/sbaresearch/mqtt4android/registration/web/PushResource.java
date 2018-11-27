package at.sbaresearch.mqtt4android.registration.web;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/push")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class PushResource {


  @RequestMapping(value = "/{registrationId}", method = RequestMethod.POST)
  public void sendMessage(@PathVariable String registrationId, @RequestBody String message) {
    log.info("push message {} for registrationId {} received", message, registrationId);
    // TODO push to app
  }
}
