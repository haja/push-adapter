package at.sbaresearch.mqtt4android.relay.jaas;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.security.JaasCertificateAuthenticationPlugin;

public class JaasCertificateOnlyAuthPlugin extends JaasCertificateAuthenticationPlugin {
  @Override
  public Broker installPlugin(Broker broker) {
    initialiseJaas();
    return new TlsCertificateAuthenticationBroker(broker, configuration);
  }
}
