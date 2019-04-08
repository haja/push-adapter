package at.sbaresearch.mqtt4android.registration;

import at.sbaresearch.mqtt4android.common.SecureRngGenerator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RegistrationService {

  private static int TOKEN_LENGTH = 32;

  RegistrationRepository repository;
  SecureRngGenerator rng;

  public String registerApp(DeviceId device, AppRegistration registration) {
    // TODO verify app cert?

    val token = rng.randomString(TOKEN_LENGTH);
    // TODO save app as well, so if re-registering old values for the same app can be dropped
    repository.register(token, device.id);

    return token;
  }

  // TODO expose repo here

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
