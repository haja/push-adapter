package at.sbaresearch.mqtt4android.relay;

import at.sbaresearch.mqtt4android.relay.mqtt.CustomAuthorizationPlugin;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.activemq.advisory.AdvisorySupport;
import org.apache.activemq.broker.BrokerPlugin;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.filter.DestinationMap;
import org.apache.activemq.jaas.GroupPrincipal;
import org.apache.activemq.security.SimpleAuthorizationMap;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TopicRegistry {
  public static final String TOPIC_WRITE_PRINCIPAL_GROUP = "system-group";
  public static final String TOPIC_READ_PRINCIPAL_GROUP = "client-group";

  AuthorizationWrapper wrapper;
  PushService pushService;

  public TopicRegistry(PushService pushService) {
    this.pushService = pushService;
    wrapper = new AuthorizationWrapper();
  }

  public String createTopic(String clientId) {
    //
    return clientId;
  }

  public BrokerPlugin getAuthorizationPlugin() {
    return wrapper.getAuthorizationPlugin();
  }


  @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
  private static class AuthorizationWrapper {

    CustomAuthorizationPlugin authorizationPlugin;
    DestinationMap readACLs;
    DestinationMap writeACLs;
    DestinationMap adminACLs;

    private AuthorizationWrapper() {
      // base ACLs for broker-intern access
      readACLs = createTopicReadACLs();
      writeACLs = createTopicWriteACLs();
      adminACLs = createTopicAdminACLs();
      authorizationPlugin = new CustomAuthorizationPlugin(new SimpleAuthorizationMap(writeACLs, readACLs, adminACLs));
    }

    public CustomAuthorizationPlugin getAuthorizationPlugin() {
      return authorizationPlugin;
    }

    private DestinationMap createTopicWriteACLs() {
      DestinationMap destinationMap = new DestinationMap();
      putTopic(destinationMap, "topic://>", TOPIC_WRITE_PRINCIPAL_GROUP);
      // allow full access to advisory topics, see http://activemq.apache.org/security
      putTopic(destinationMap, "topic://ActiveMQ.Advisory>", TOPIC_READ_PRINCIPAL_GROUP);
      putTopic(destinationMap, "topic://ActiveMQ.Advisory.Topic", TOPIC_READ_PRINCIPAL_GROUP);
      return destinationMap;
    }

    private DestinationMap createTopicReadACLs() {
      DestinationMap destinationMap = new DestinationMap();

      // allow writer reading
      putTopic(destinationMap, "topic://>", TOPIC_WRITE_PRINCIPAL_GROUP);
      // allow full access to advisory topics, see http://activemq.apache.org/security
      putTopic(destinationMap, "topic://ActiveMQ.Advisory>", TOPIC_READ_PRINCIPAL_GROUP);
      putTopic(destinationMap, "topic://ActiveMQ.Advisory.Topic", TOPIC_READ_PRINCIPAL_GROUP);

      return destinationMap;
    }

    private DestinationMap createTopicAdminACLs() {
      return createTopicWriteACLs();
    }

    private void putTopic(DestinationMap destinationMap, String topic, String group) {
      ActiveMQDestination destination = ActiveMQDestination.createDestination(
          topic,
          ActiveMQDestination.TOPIC_TYPE);
      putDestination(destinationMap, destination, group);
    }

    private void putDestination(DestinationMap destinationMap, ActiveMQDestination destination,
        String group) {
      destinationMap.put(destination,
          new GroupPrincipal(group)
      );
    }
  }
}
