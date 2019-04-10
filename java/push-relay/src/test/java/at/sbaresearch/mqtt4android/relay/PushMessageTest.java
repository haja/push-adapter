package at.sbaresearch.mqtt4android.relay;

import at.sbaresearch.mqtt4android.AppTest;
import at.sbaresearch.mqtt4android.TestData;
import at.sbaresearch.mqtt4android.relay.web.PushResource;
import lombok.AllArgsConstructor;
import lombok.val;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.*;

public class PushMessageTest extends AppTest {

  @Autowired
  PushResource pushResource;
  @Autowired
  TestData testData;

  @Test
  public void pushMessage_validToken_shouldSucceed() throws Exception {
    val reg = testData.registrations.registration1;
    val msg = "this is a test";
    pushResource.sendMessage(reg.getToken(), msg);

    // TODO assert that message was pushed
    fail("implement");
  }

  @Test
  public void pushMessage_invalidToken_shouldFail() throws Exception {
    val reg = testData.registrations.registration1;
    val msg = "this should fail";
    assertThatThrownBy(() -> {
      pushResource.sendMessage(reg.getToken() + "-modified", msg);
    }).isInstanceOf(RuntimeException.class);
  }
}
