package at.sbaresearch.mqtt4android.integration;

import at.sbaresearch.mqtt4android.registration.web.RegistrationResource;
import lombok.val;
import org.fusesource.mqtt.client.MQTT;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import static at.sbaresearch.mqtt4android.integration.RegistrationTestHelper.deviceReq;
import static org.assertj.core.api.Assertions.*;

public class DeviceRegistrationTest extends AppTest {

  @Autowired
  RegistrationResource resource;

  @Value("${mqtt.hostname}") String mqttHostname;
  @Value("${mqtt.port}") int mqttPort;

  @Test
  public void testRegistration_shouldBeNotNull() throws Exception {
    val reg = resource.registerDevice(deviceReq().build());
    assertThat(reg).isNotNull();
    assertThat(reg).hasNoNullFieldsOrProperties();
  }

  @Test
  public void testRegistrationTwice_shouldReturnDifferentTopic() throws Exception {
    val reg = resource.registerDevice(deviceReq().build());
    val reg2 = resource.registerDevice(deviceReq().build());
    assertThat(reg.getMqttTopic()).isNotEqualToIgnoringCase(reg2.getMqttTopic());
  }

  @Test
  public void testRegistration_shouldConnectThroughTls() throws Exception {
    val reg = resource.registerDevice(deviceReq().build());

    MQTT client = new MQTT();
    client.setHost("ssl://" + mqttHostname + ":" + mqttPort);
    // TODO setup ssl connection (how to share this with android?)
    // client.setSslContext();

    // try connection
    // push hello to REST endpoint
    // assert hello on mqtt client
    fail("not implemented");
  }
}
