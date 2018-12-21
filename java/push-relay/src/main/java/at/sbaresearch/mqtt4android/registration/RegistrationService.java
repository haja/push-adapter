package at.sbaresearch.mqtt4android.registration;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RegistrationService {

  RegistrationStore registrationStore;

  public String registerApp(DeviceId device, AppRegistration registration) {
    // TODO mocked for now
    // TODO legitimate client to receive notifications for this app
    registrationStore.register();

    return "mockedRegToken";
  }

  @Value
  public static class AppRegistration {
    String app;
    String cert;
  }

  @Value
  public static class DeviceId {
    String id;
  }
}
