package at.sbaresearch.mqtt4android.relay.jaas;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.EmptyBroker;
import org.apache.activemq.security.JaasCertificateAuthenticationPlugin;
import org.apache.activemq.security.SimpleAuthenticationBroker;
import org.apache.activemq.security.SimpleAuthenticationPlugin;

public class JaasCertOnlyOrSimpleAuthenticationPlugin extends JaasCertificateAuthenticationPlugin {

  private SimpleAuthenticationPlugin simpleAuthPlugin;

  public JaasCertOnlyOrSimpleAuthenticationPlugin(SimpleAuthenticationPlugin simpleAuthPlugin) {
    super();
    this.simpleAuthPlugin = simpleAuthPlugin;
  }

  @Override
  public Broker installPlugin(Broker broker) {
    SimpleAuthenticationBroker simpleBroker = (SimpleAuthenticationBroker) simpleAuthPlugin
        .installPlugin(new EmptyBroker());
    initialiseJaas();
    return new JaasCertificateAndSimpleAuthenticationBroker(broker, configuration, simpleBroker);
  }
}
