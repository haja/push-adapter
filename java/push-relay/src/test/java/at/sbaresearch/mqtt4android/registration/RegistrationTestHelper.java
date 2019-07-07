package at.sbaresearch.mqtt4android.registration;

import at.sbaresearch.mqtt4android.registration.web.RegistrationResource.AppRegistrationRequest;
import at.sbaresearch.mqtt4android.registration.web.RegistrationResource.AppRegistrationRequest.AppRegistrationRequestBuilder;
import at.sbaresearch.mqtt4android.registration.web.RegistrationResource.DeviceRegistrationRequest;
import at.sbaresearch.mqtt4android.registration.web.RegistrationResource.DeviceRegistrationRequest.DeviceRegistrationRequestBuilder;

public class RegistrationTestHelper {
  public static DeviceRegistrationRequestBuilder deviceReq() {
    return DeviceRegistrationRequest.testWith()
        .dummy("foobar");
  }

  public static AppRegistrationRequestBuilder appReq() {
    return AppRegistrationRequest.testWith()
        .senderId("someSenderId")
        .app("my.test.app");
  }
}
