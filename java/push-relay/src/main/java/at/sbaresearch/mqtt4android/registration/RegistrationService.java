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

  public String registerApp(AppRegistration registration) {
    // TODO verify app signature?

    val token = rng.randomString(TOKEN_LENGTH);
    repository.register(registration, token);

    return token;
  }

  public AppRegistration getApp(String token) {
    return repository.getTopic(token);
  }

  @Value
  public static class AppRegistration {
    String app;
    String signature;
    DeviceId deviceId;
    String senderId;
  }

  @Value
  public static class DeviceId {
    String id;
  }
}
