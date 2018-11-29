package at.sbaresearch.mqtt4android.registration.web;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/registration")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class RegistrationResource {

  @RequestMapping(path = "/new", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public RegistrationResponse registerDevice(@RequestBody RegistrationRequest req) {
    log.info("register device: {}", req);
    // TODO mocked for now
    // TODO legitimate client to receive notifications for this app
    return new RegistrationResponse("testRegisterId", null);
  }

  @RequestMapping(path = "/list", method = RequestMethod.GET)
  public String listAll() {
    log.info("listing");
    // TODO mocked for now
    return "testRegisterId....";
  }

  @Value
  public static class RegistrationRequest {
    String app;
    String cert;
    Integer appVer;
    String appVerName;
    String info;
//    Boolean delete;
//    int osv;
//    int gmsv;
//    String scope;
//    String appid;
//    String gmpAppId;
  }

  @Value
  private class RegistrationResponse {
    @NonNull
    String token;
    String deleted;
  }

}
