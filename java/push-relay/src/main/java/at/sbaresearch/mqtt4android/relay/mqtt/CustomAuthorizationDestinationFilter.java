package at.sbaresearch.mqtt4android.relay.mqtt;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.broker.region.DestinationFilter;
import org.apache.activemq.broker.region.Subscription;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CustomAuthorizationDestinationFilter extends DestinationFilter {

  CustomAuthorizationBroker broker;

  public CustomAuthorizationDestinationFilter(Destination destination, CustomAuthorizationBroker broker) {
    super(destination);
    this.broker = broker;
  }

  @Override
  public void addSubscription(ConnectionContext context, Subscription sub) throws Exception {
    val securityContext = broker.checkSecurityContext(context);

    // use the destination being filtered, instead of the destination from the consumerinfo in the subscription
    // since that could be a wildcard destination
    val destination = next.getActiveMQDestination();

    val topicAllowed = broker.isTopicAllowed(securityContext, destination);
    val systemUser = broker.isSystemUser(securityContext);

    if (securityContext.isBrokerContext() || topicAllowed || systemUser) {
      super.addSubscription(context, sub);
      return;
    }
    throw new SecurityException("User " + securityContext.getUserName() + " is not authorized to read from: " + destination);
  }
}
