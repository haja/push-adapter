package at.sbaresearch.mqtt4android.relay.mqtt;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerPlugin;
import org.apache.activemq.security.AuthorizationMap;

import java.util.Objects;

public class CustomAuthorizationPlugin implements BrokerPlugin {

  private AuthorizationMap fallbackMap;

  public CustomAuthorizationPlugin(AuthorizationMap fallbackMap) {
    Objects.requireNonNull(fallbackMap);
    this.fallbackMap = fallbackMap;
  }

  public Broker installPlugin(Broker broker) {
    return new CustomAuthorizationBroker(broker, fallbackMap);
  }
}
