package at.sbaresearch.mqtt4android.relay.jaas;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.command.ConnectionInfo;
import org.apache.activemq.command.ConsumerInfo;
import org.apache.activemq.command.ProducerInfo;
import org.apache.activemq.security.JaasCertificateAuthenticationBroker;

public class TlsCertificateAuthenticationBroker extends JaasCertificateAuthenticationBroker {
  public TlsCertificateAuthenticationBroker(Broker broker, String configuration) {
    super(broker, configuration);
  }

  @Override
  public void addConnection(ConnectionContext context, ConnectionInfo info) throws Exception {
    // TODO allow all connections that have a valid certificate (from our CA)
    if(context.getConnection().isNetworkConnection()) {

    }
    super.addConnection(context, info);
  }

  @Override
  public Subscription addConsumer(ConnectionContext context, ConsumerInfo info) throws Exception {
    // TODO allow consumers only for the topic their certificate was issued for
    // TODO is AuthorizationBroker / AuthorizationMap intended for this and sufficient?
    return super.addConsumer(context, info);
  }

  @Override
  public void addProducer(ConnectionContext context, ProducerInfo info) throws Exception {
    // TODO do we need to restrict access here?
    //  or any other method of this class?
    super.addProducer(context, info);
  }
}
