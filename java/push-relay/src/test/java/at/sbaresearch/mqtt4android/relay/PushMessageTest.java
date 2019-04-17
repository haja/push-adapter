package at.sbaresearch.mqtt4android.relay;

import at.sbaresearch.mqtt4android.AppTest;
import at.sbaresearch.mqtt4android.PushTestHelper;
import at.sbaresearch.mqtt4android.TestData;
import at.sbaresearch.mqtt4android.relay.web.PushResource;
import lombok.val;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static at.sbaresearch.mqtt4android.PushTestHelper.*;
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
        .token(reg.getToken());
    pushResource.sendMessage(toReq(msg));
  }

  @Test
  public void pushMessage_invalidToken_shouldFail() throws Exception {
    val reg = testData.registrations.registration1;
    val msg = helper.pushMessageBuilder()
        .token(reg.getToken() + "-modified");
    assertThatThrownBy(() -> pushResource.sendMessage(toReq(msg)))
        .isInstanceOf(RuntimeException.class);
  }
}
