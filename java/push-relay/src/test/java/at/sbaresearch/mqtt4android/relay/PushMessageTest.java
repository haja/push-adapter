package at.sbaresearch.mqtt4android.relay;

import at.sbaresearch.mqtt4android.AppTest;
import at.sbaresearch.mqtt4android.PushTestHelper;
import at.sbaresearch.mqtt4android.TestData;
import at.sbaresearch.mqtt4android.relay.web.PushResource;
import lombok.val;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.*;

public class PushMessageTest extends AppTest {

  @Autowired
  PushResource pushResource;
  @Autowired
  TestData testData;
  @Autowired
  PushTestHelper helper;

  @Test
  public void pushMessage_validToken_shouldNotThrow() throws Exception {
    val reg = testData.registrations.registration1;
    val msg = helper.pushMessageBuilder()
        .token(reg.getToken())
        .build();
    pushResource.sendMessage(msg);
  }

  @Test
  public void pushMessage_invalidToken_shouldFail() throws Exception {
    val reg = testData.registrations.registration1;
    val msg = helper.pushMessageBuilder()
        // TODO remove name, is "output only" identifier in FCM
        .name("this should fail")
        .token(reg.getToken() + "-modified")
        .build();
    assertThatThrownBy(() -> pushResource.sendMessage(msg))
        .isInstanceOf(RuntimeException.class);
  }
}
