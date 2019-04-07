package at.sbaresearch.mqtt4android.registration;

import at.sbaresearch.mqtt4android.AppTest;
import at.sbaresearch.mqtt4android.registration.web.RegistrationResource;
import lombok.val;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static at.sbaresearch.mqtt4android.registration.RegistrationTestHelper.deviceReq;
import static org.assertj.core.api.Assertions.*;

public class AppRegistrationTest extends AppTest {

  @Autowired
  RegistrationResource resource;

  @Test
  public void testAppRegistration_shouldSucceed() throws Exception {
    defaultRegistration();

    // TODO request app registration through HTTPS with client cert
    //resource.registerApp(appReq().build());
    fail("not implemented");
  }

  private void defaultRegistration() throws Exception {
    val reg = resource.registerDevice(deviceReq().build());
  }

}
