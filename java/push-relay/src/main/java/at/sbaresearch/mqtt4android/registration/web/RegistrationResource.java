package at.sbaresearch.mqtt4android.registration.web;

import at.sbaresearch.mqtt4android.registration.DeviceService;
import at.sbaresearch.mqtt4android.registration.DeviceService.DeviceData;
import at.sbaresearch.mqtt4android.registration.RegistrationService;
import at.sbaresearch.mqtt4android.registration.RegistrationService.AppRegistration;
import at.sbaresearch.mqtt4android.registration.RegistrationService.DeviceId;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
@AllArgsConstructor
public class RegistrationResource {

  public static final String REGISTRATION_DEVICE = "/registration/device";
  public static final String REGISTRATION_APP = "/registration/new";

  RegistrationService registrationService;
  DeviceService deviceService;

  // TODO exception handling

  // TODO are parameters needed at all?
  @PostMapping(path = REGISTRATION_DEVICE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public DeviceRegisterDto registerDevice(@RequestBody DeviceRegistrationRequest req) throws Exception {
    // TODO what data do we need from the client?
    return toDeviceRegisterDto(deviceService.registerDevice());
  }

  private DeviceRegisterDto toDeviceRegisterDto(DeviceData data) {
    val conn = data.getMqttConnection();
    val keys = data.getClientKeys();
    return new DeviceRegisterDto(conn._1, conn._2,
        data.getMqttTopic(),
        keys.getEncodedPrivateKey(),
        keys.getEncodedCert());
  }

  // TODO get deviceId from client TLS cert. how to do this with spring?
  // TODO actually, this is registerApp; rename endpoint to "app" or so
  @PostMapping(path = REGISTRATION_APP, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public AppRegistrationResponse registerApp(@RequestBody AppRegistrationRequest req,
      @AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
    log.info("register app: {} for user: {}", req, user);

    val deviceId = toDeviceId(user);
    val registrationData = mapFromRequest(req);
    val token = registrationService.registerApp(deviceId, registrationData);

    return new AppRegistrationResponse(token);
  }

  private DeviceId toDeviceId(org.springframework.security.core.userdetails.User user) {
    return new DeviceId(user.getUsername());
  }

  private AppRegistration mapFromRequest(AppRegistrationRequest req) {
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
  @Builder(builderMethodName = "testWith")
  public static class AppRegistrationRequest {
    String app;
    String cert;
    Integer appVer;
    String appVerName;
    String info;
    //    Boolean delete;
    //    int osv;
    //    int gmsv;
    //    String scope;
    //    TODO appID will be most likely needed
    //    String appid;
    //    String gmpAppId;
  }

  // TODO this should be a static class
  @Value
  public class AppRegistrationResponse {
    @NonNull
    String token;
  }

  @Value
  @Builder(builderMethodName = "testWith")
  public static class DeviceRegistrationRequest {
    String dummy;
  }

  @Builder(builderMethodName = "testWith", toBuilder = true)
  @Value
  public static class DeviceRegisterDto {
    @NonNull
    String host;
    int port;
    @NonNull
    String mqttTopic;
    @NonNull
    byte[] encodedPrivateKey;
    @NonNull
    byte[] encodedCert;
  }
}
