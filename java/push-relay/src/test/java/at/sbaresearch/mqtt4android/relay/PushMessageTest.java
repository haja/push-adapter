package at.sbaresearch.mqtt4android.relay;

import at.sbaresearch.mqtt4android.AppTest;
import at.sbaresearch.mqtt4android.relay.web.PushResource;
import lombok.val;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.*;

public class PushMessageTest extends AppTest {

  @Autowired
  PushResource pushResource;

  @Test
  public void pushMessage_validToken_shouldSucceed() throws Exception {
    val reg = PushTestData.registration1;
    val msg = "this is a test";
    pushResource.sendMessage(reg.getToken(), msg);

    fail("implement");
  }

  @Test
  public void pushMessage_invalidToken_shouldFail() throws Exception {
    val reg = PushTestData.registration1;
    val msg = "this should fail";
    assertThatThrownBy(() -> {
      pushResource.sendMessage(reg.getToken() + "-modified", msg);
    }).isInstanceOf(RuntimeException.class);
  }
}
