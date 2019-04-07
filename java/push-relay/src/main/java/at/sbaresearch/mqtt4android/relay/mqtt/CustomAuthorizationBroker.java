package at.sbaresearch.mqtt4android.relay.mqtt;

import lombok.val;
import org.apache.activemq.advisory.AdvisorySupport;
import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.broker.region.CompositeDestinationInterceptor;
import org.apache.activemq.broker.region.DestinationInterceptor;
import org.apache.activemq.broker.region.RegionBroker;
import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ConsumerInfo;
import org.apache.activemq.security.AuthorizationBroker;
import org.apache.activemq.security.AuthorizationDestinationInterceptor;
import org.apache.activemq.security.AuthorizationMap;
import org.apache.activemq.security.SecurityContext;

import java.util.Arrays;

/**
 * allow authenticated users to read and admin the topic matching their username
 *
 * if not allowed, fallback to fallback map.
 */
public class CustomAuthorizationBroker extends AuthorizationBroker {
  public CustomAuthorizationBroker(Broker next,
      AuthorizationMap fallbackMap) {
    super(next, fallbackMap);

    // replace DestinationInterceptor
    final RegionBroker regionBroker = (RegionBroker) next.getAdaptor(RegionBroker.class);
    final CompositeDestinationInterceptor compositeInterceptor = (CompositeDestinationInterceptor) regionBroker.getDestinationInterceptor();
    DestinationInterceptor[] interceptors = compositeInterceptor.getInterceptors();
    interceptors = Arrays.copyOf(interceptors, interceptors.length);
    interceptors[interceptors.length - 1] = new CustomAuthorizationDestinationInterceptor(this);
    compositeInterceptor.setInterceptors(interceptors);
  }

  @Override
  protected boolean checkDestinationAdmin(SecurityContext securityContext,
      ActiveMQDestination destination) {
    boolean topicAllowed = isTopicAllowed(securityContext, destination);
    return topicAllowed ||
        super.checkDestinationAdmin(securityContext, destination);
  }

  @Override
  protected SecurityContext checkSecurityContext(ConnectionContext context)
      throws SecurityException {
    return super.checkSecurityContext(context);
  }

  @Override
  public Subscription addConsumer(ConnectionContext context, ConsumerInfo info) throws Exception {
    val securityContext = checkSecurityContext(context);

    val topicAllowed = isTopicAllowed(securityContext, info.getDestination());
    if (topicAllowed) {
      return getNext().addConsumer(context, info);
    }
    return super.addConsumer(context, info);
  }

  protected boolean isTopicAllowed(SecurityContext securityContext, ActiveMQDestination destination) {
    if (securityContext.isBrokerContext()) {
      return true;
    }
    val authUser = getUserWithoutCn(securityContext.getUserName());

    val topicMatch = topicMatches(destination, authUser);
    val advisoryMatch = topicMatches(destination, withAdvisory(authUser));
    return topicMatch || advisoryMatch;
  }

  private String getUserWithoutCn(String userName) {
    return userName.replaceFirst("CN=", "");
  }

  private boolean topicMatches(ActiveMQDestination topic, String authUser) {
    return authUser.equals(topic.getPhysicalName());
  }

  private String withAdvisory(String topic) {
    return AdvisorySupport.TOPIC_CONSUMER_ADVISORY_TOPIC_PREFIX + topic;
  }

  public boolean isSystemUser(SecurityContext securityContext) {
    return MqttBrokerConfig.TOPIC_WRITE_USERNAME.equals(securityContext.getUserName());
  }
}
