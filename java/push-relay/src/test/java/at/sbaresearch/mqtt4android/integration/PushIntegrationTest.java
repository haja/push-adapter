package at.sbaresearch.mqtt4android.integration;

import at.sbaresearch.mqtt4android.AppTest;
import at.sbaresearch.mqtt4android.MqttTestHelper;
import at.sbaresearch.mqtt4android.TestData;
import at.sbaresearch.mqtt4android.registration.web.RegistrationResource.DeviceRegisterDto.DeviceRegisterDtoBuilder;
import at.sbaresearch.mqtt4android.relay.web.PushResource;
import io.vavr.CheckedConsumer;
import lombok.val;
import org.fusesource.mqtt.client.FutureConnection;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.*;

public class PushIntegrationTest extends AppTest {
  @Autowired
  PushResource pushResource;
  @Autowired
  TestData testData;
  @Autowired
  MqttTestHelper mqtt;

  @Test
  public void pushMessage_validToken_subscribeAfterPush_shouldBeReceived() throws Throwable {
    val reg = testData.registrations.registration1;
    val pushedMsg = "push message IT";
    pushResource.sendMessage(reg.getToken(), pushedMsg);

    // TODO this test fails, maybe we need to set a different QoS?
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
      pushResource.sendMessage(reg.getToken(), pushedMsg);

      val msg = mqtt.await(conn.receive());
      assertThat(new String(msg.getPayload())).contains(pushedMsg);
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
