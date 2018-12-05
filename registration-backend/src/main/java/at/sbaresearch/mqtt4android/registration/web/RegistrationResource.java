package at.sbaresearch.mqtt4android.registration.web;

import at.sbaresearch.mqtt4android.registration.RegistrationService;
import at.sbaresearch.mqtt4android.registration.RegistrationService.AppRegistration;
import at.sbaresearch.mqtt4android.registration.RegistrationService.DeviceId;
import lombok.*;
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
@AllArgsConstructor
public class RegistrationResource {

  RegistrationService registrationService;

  @RequestMapping(path = "/new", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public RegistrationResponse registerDevice(@RequestBody RegistrationRequest req) {
    log.info("register device: {}", req);

    // TODO deviceID should be extracted from clientCert the adapter-backend got on device registration
    val deviceId = new DeviceId("1234foobar");
    val registrationData = mapFromRequest(req);
    val token = registrationService.registerApp(deviceId, registrationData);

    return new RegistrationResponse(token, null);
  }

  private AppRegistration mapFromRequest(RegistrationRequest req) {
    return new AppRegistration(req.app, req.cert);
  }

  @RequestMapping(path = "/list", method = RequestMethod.GET)
  public String listAll() {
    log.info("listing");
    // TODO this must not be exposed
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
