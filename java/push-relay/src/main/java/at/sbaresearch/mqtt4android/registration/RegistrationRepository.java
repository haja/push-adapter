package at.sbaresearch.mqtt4android.registration;

import at.sbaresearch.mqtt4android.registration.RegistrationService.AppRegistration;

public interface RegistrationRepository {
  void register(AppRegistration registration, String token);
  AppRegistration getTopic(String token);
}
