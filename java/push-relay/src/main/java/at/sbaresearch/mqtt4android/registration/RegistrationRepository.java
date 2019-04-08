package at.sbaresearch.mqtt4android.registration;

public interface RegistrationRepository {
  void register(String token, String topic);
  /**
   *  TODO token + app + topic
   */
  String getTopic(String token);
}
