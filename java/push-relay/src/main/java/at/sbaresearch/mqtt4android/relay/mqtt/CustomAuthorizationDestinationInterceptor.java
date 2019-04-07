package at.sbaresearch.mqtt4android.relay.mqtt;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.broker.region.DestinationInterceptor;
import org.apache.activemq.command.ActiveMQDestination;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class CustomAuthorizationDestinationInterceptor
    implements DestinationInterceptor {

  CustomAuthorizationBroker broker;

  @Override
  public Destination intercept(Destination destination) {
    return new CustomAuthorizationDestinationFilter(destination, broker);
  }

  @Override
  public void remove(Destination destination) {
    // nop
  }

  @Override
  public void create(Broker broker, ConnectionContext context, ActiveMQDestination destination)
      throws Exception {
    // nop
  }
}
