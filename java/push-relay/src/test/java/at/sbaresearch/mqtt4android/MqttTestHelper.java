package at.sbaresearch.mqtt4android;

import at.sbaresearch.mqtt4android.pinning.ClientKeyCert;
import at.sbaresearch.mqtt4android.pinning.PinningSslFactory;
import at.sbaresearch.mqtt4android.registration.web.RegistrationResource.DeviceRegisterDto;
import io.vavr.CheckedConsumer;
import lombok.val;
import org.fusesource.mqtt.client.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.concurrent.TimeUnit;

@Profile("it")
@Component
public class MqttTestHelper {
  @Autowired
  @Qualifier("serverCert")
  Certificate serverCert;

  public MQTT setupClient(DeviceRegisterDto reg) throws Exception {
    val pinningFactory = createPinningFactory(reg.getEncodedPrivateKey(), reg.getEncodedCert());
    val client = new MQTT();
    client.setHost("ssl://" + reg.getHost() + ":" + reg.getPort());
    client.setSslContext(pinningFactory.getSslContext());
    return client;
  }

  public void withConnection(final MQTT client, CheckedConsumer<FutureConnection> withConnection)
      throws Throwable {
    val connection = client.futureConnection();
    connection.connect().await(2L, TimeUnit.SECONDS);
    try {
      withConnection.accept(connection);
    } finally {
      connection.disconnect();
    }
  }

  public void subscribe(FutureConnection connection, String topic)
      throws Exception {
    await(connection.subscribe(new Topic[]{
        new Topic(topic, QoS.AT_LEAST_ONCE)
    }));
  }

  private PinningSslFactory createPinningFactory(
      final byte[] privateKey, final byte[] cert) throws Exception {
    val keys = new ClientKeyCert(privateKey, cert);
    val in = toInputStream(serverCert);
    return new PinningSslFactory(keys, in);
  }

  private InputStream toInputStream(Certificate cert) throws CertificateEncodingException {
    return new ByteArrayInputStream(cert.getEncoded());
  }

  public <T> T await(final Future<T> future) throws Exception {
    return future.await(500L, TimeUnit.MILLISECONDS);
  }
}
