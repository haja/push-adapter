package at.sbaresearch.mqtt4android.integration;

import at.sbaresearch.mqtt4android.AppTest;
import at.sbaresearch.mqtt4android.registration.web.RegistrationResource;
import lombok.val;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static at.sbaresearch.mqtt4android.integration.RegistrationTestHelper.deviceReq;
import static org.assertj.core.api.Assertions.*;

public class AppRegistrationTest extends AppTest {

  @Autowired
  RegistrationResource resource;

  @Test
  public void testAppRegistration_shouldSucceed() throws Exception {
    // TODO setup registered device
    val reg = resource.registerDevice(deviceReq().build());

    // TODO request app registration through HTTPS with client cert
    //resource.registerApp(appReq().build());
    fail("not implemented");
  }

}
