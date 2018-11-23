package at.sbaresearch.mqtt4android.registration.web;

import lombok.AccessLevel;
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

  @RequestMapping(path = "/new", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public String registerDevice(RegistrationRequest req) {
    log.info("register device: {}", req);
    // TODO mocked for now
    return "testRegisterId";
  }

  @RequestMapping(path = "/list", method = RequestMethod.GET)
  public String listAll() {
    log.info("listing");
    // TODO mocked for now
    return "testRegisterId....";
  }

  @Value
  private class RegistrationRequest {
    String app;
    String cert;
    String appVer;
    String appVerName;
    String info;
//    boolean delete;
//    int osv;
//    int gmsv;
//    String scope;
//    String appid;
//    String gmpAppId;
  }
}
