package at.sbaresearch.mqtt4android.integration;

import at.sbaresearch.mqtt4android.pinning.ConnectionSettings;
import at.sbaresearch.mqtt4android.pinning.PinningSslFactory;
import at.sbaresearch.mqtt4android.registration.web.RegistrationResource;
import at.sbaresearch.mqtt4android.registration.web.RegistrationResource.DeviceRegisterDto;
import at.sbaresearch.mqtt4android.relay.web.PushResource;
import io.vavr.CheckedConsumer;
import lombok.val;
import org.assertj.core.api.SoftAssertions;
import org.fusesource.mqtt.client.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.concurrent.TimeUnit;

import static at.sbaresearch.mqtt4android.integration.RegistrationTestHelper.appReq;
import static at.sbaresearch.mqtt4android.integration.RegistrationTestHelper.deviceReq;
import static org.assertj.core.api.Assertions.*;

public class DeviceRegistrationTest extends AppTest {

  @Autowired
  RegistrationResource registrationResource;
  @Autowired
  PushResource pushResource;

  @Autowired
  @Qualifier("serverCert")
  Certificate serverCert;

  @Test
  public void testRegistration_shouldBeNotNull() throws Exception {
    val reg = registrationResource.registerDevice(deviceReq().build());
    assertThat(reg).isNotNull();
    assertThat(reg).hasNoNullFieldsOrProperties();
  }

  @Test
  public void testRegistrationTwice_shouldReturnDifferentTopicAndCredentials() throws Exception {
    val reg = registrationResource.registerDevice(deviceReq().build());
    val reg2 = registrationResource.registerDevice(deviceReq().build());

    SoftAssertions.assertSoftly(softly -> {
      softly.assertThat(reg.getMqttTopic()).isNotEqualToIgnoringCase(reg2.getMqttTopic());
      softly.assertThat(reg.getEncodedCert()).isNotEqualTo(reg2.getEncodedCert());
      softly.assertThat(reg.getEncodedPrivateKey()).isNotEqualTo(reg2.getEncodedPrivateKey());
    });
  }

  // TODO this test depends on our external hostname as configured in application-it.yml
  @Test
  public void testRegistration_shouldConnectThroughTls() throws Throwable {
    val reg = registrationResource.registerDevice(deviceReq().build());
    val settings = toSettings(reg);

    withConnection(setupClient(reg, settings), connection -> {
      subscribe(connection, settings);

      val mockUser = registrationToUser(reg);
      val appResp = registrationResource.registerApp(appReq().build(), mockUser);

      String messageContent = "some push content";
      pushResource.sendMessage(appResp.getToken(), messageContent);

      val message = await(connection.receive());
      // TODO better assert of payload
      assertThat(new String(message.getPayload())).contains(messageContent);
    });
  }

  private User registrationToUser(DeviceRegisterDto reg) {
    // TODO go through spring security?
    val usr = new User();
    usr.setName(reg.getMqttTopic());
    return usr;
  }

  private void withConnection(final MQTT client, CheckedConsumer<FutureConnection> withConnection)
      throws Throwable {
    val connection = client.futureConnection();
    connection.connect().await(2L, TimeUnit.SECONDS);
    try {
      withConnection.accept(connection);
    } finally {
      connection.disconnect();
    }
  }

  private MQTT setupClient(DeviceRegisterDto reg, ConnectionSettings settings) throws Exception {
    val pinningFactory = createPinningFactory(reg);
    val client = new MQTT();
    client.setHost(settings.getServerUrl());
    client.setSslContext(pinningFactory.getSslContext());
    return client;
  }

  private void subscribe(FutureConnection connection, ConnectionSettings settings)
      throws Exception {
    await(connection.subscribe(new Topic[]{
        new Topic(settings.getTopic(), QoS.AT_LEAST_ONCE)
    }));
  }

  private PinningSslFactory createPinningFactory(
      DeviceRegisterDto reg) throws Exception {
    val conn = toSettings(reg);
    val in = toInputStream(serverCert);
    return new PinningSslFactory(conn, in);
  }

  private InputStream toInputStream(Certificate cert) throws CertificateEncodingException {
    return new ByteArrayInputStream(cert.getEncoded());
  }

  private ConnectionSettings toSettings(DeviceRegisterDto reg) {
    return new ConnectionSettings(reg.getHost(), reg.getPort(), reg.getMqttTopic(),
        reg.getEncodedPrivateKey(), reg.getEncodedCert());
  }

  private <T> T await(final Future<T> future) throws Exception {
    return future.await(500L, TimeUnit.MILLISECONDS);
  }
}
