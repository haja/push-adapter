package at.sbaresearch.mqtt4android.integration;

import at.sbaresearch.mqtt4android.AppTest;
import at.sbaresearch.mqtt4android.MqttTestHelper;
import at.sbaresearch.mqtt4android.PushTestHelper;
import at.sbaresearch.mqtt4android.TestData;
import at.sbaresearch.mqtt4android.registration.web.RegistrationResource.DeviceRegisterDto.DeviceRegisterDtoBuilder;
import at.sbaresearch.mqtt4android.relay.web.PushResource;
import io.vavr.CheckedConsumer;
import lombok.val;
import org.fusesource.mqtt.client.FutureConnection;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeoutException;

import static at.sbaresearch.mqtt4android.PushTestHelper.*;
import static org.assertj.core.api.Assertions.*;

public class PushIntegrationTest extends AppTest {
  @Autowired
  PushResource pushResource;
  @Autowired
  TestData testData;
  @Autowired
  MqttTestHelper mqtt;
  @Autowired
  PushTestHelper helper;

  @Test
  @Ignore("investigate QueryBasedSubscriptionRecoveryPolicy")
  public void pushMessage_validToken_subscribeAfterPush_shouldBeReceived() throws Throwable {
    val reg = testData.registrations.registration1;
    val pushedMsg = "push message IT";
    val pushDto = helper.pushMessageBuilder(pushedMsg)
        .token(reg.getToken());
    pushResource.sendMessage(toReq(pushDto));

    // TODO this test fails; use a QueryBasedSubscriptionRecoveryPolicy https://activemq.apache.org/subscription-recovery-policy to fix this
    withClient(testData.clients.client1.mqttTopic(reg.getTopic()), conn -> {
      val msg = mqtt.await(conn.receive());
      assertThat(new String(msg.getPayload())).contains(pushedMsg);
    });
  }

  @Test
  public void pushMessage_validToken_subscribeBeforePush_shouldBeReceived() throws Throwable {
    val reg = testData.registrations.registration1;

    withClient(testData.clients.client1.mqttTopic(reg.getTopic()), conn -> {
      val pushedMsg = "push message IT2";
      val pushDto = helper.pushMessageBuilder(pushedMsg)
          .token(reg.getToken());
      pushResource.sendMessage(toReq(pushDto));

      val msg = mqtt.await(conn.receive());
      assertThat(new String(msg.getPayload())).contains(pushedMsg);
    });
  }

  @Test
  public void pushMessage_otherClient_sameApp_shouldNotReceive() throws Throwable {
    val client1 = testData.registrations.registration1;
    val client2 = testData.registrations.registration2;

    withClient(testData.clients.client1.mqttTopic(client1.getTopic()), conn -> {
      val msgForOtherClient = helper.pushMessageBuilder()
          .token(client2.getToken());
      pushResource.sendMessage(toReq(msgForOtherClient));

      assertThatThrownBy(() -> mqtt.await(conn.receive()))
          .isInstanceOf(TimeoutException.class);
    });
  }


  private void withClient(DeviceRegisterDtoBuilder registrationBuilder,
      CheckedConsumer<FutureConnection> afterSubscribe)
      throws Throwable {
    val reg = registrationBuilder.build();
    val client = this.mqtt.setupClient(reg);
    mqtt.withConnection(client, conn -> {
      mqtt.subscribe(conn, reg.getMqttTopic());
      afterSubscribe.accept(conn);
    });
  }

}
