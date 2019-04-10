package at.sbaresearch.mqtt4android.registration;

import at.sbaresearch.mqtt4android.HttpTest;
import at.sbaresearch.mqtt4android.TestData;
import at.sbaresearch.mqtt4android.registration.crypto.CryptoTestHelper;
import at.sbaresearch.mqtt4android.registration.web.RegistrationResource;
import at.sbaresearch.mqtt4android.registration.web.RegistrationResource.AppRegistrationResponse;
import lombok.val;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.*;

public class AppRegistrationHttpsTest extends HttpTest {
  @Autowired
  private TestData testData;
  @Autowired
  private CryptoTestHelper cryptoHelper;

  @Test
  public void testAppRegistration_shouldSucceed() throws Exception {
    val req = RegistrationResource.AppRegistrationRequest.testWith()
        .app("my.test.app")
        .build();

    val restTemplate = cryptoHelper.addClientKeys(this.restBuilder, testData.clients.client1Keys)
        .build();

    assertThat(restTemplate.postForObject(RegistrationResource.REGISTRATION_APP,
        req, AppRegistrationResponse.class).getToken())
        .isNotEmpty();
  }
}
