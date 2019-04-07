package at.sbaresearch.mqtt4android.relay.jaas;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.security.JaasCertificateAuthenticationBroker;

public class TlsCertificateAuthenticationBroker extends JaasCertificateAuthenticationBroker {
  public TlsCertificateAuthenticationBroker(Broker broker, String configuration) {
    super(broker, configuration);
  }
}
