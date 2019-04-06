package at.sbaresearch.mqtt4android.relay;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.apache.activemq.advisory.AdvisorySupport;
import org.apache.activemq.broker.BrokerPlugin;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.filter.DestinationMap;
import org.apache.activemq.jaas.GroupPrincipal;
import org.apache.activemq.security.AuthorizationPlugin;
import org.apache.activemq.security.SimpleAuthorizationMap;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TopicRegistry {
  public static final String TOPIC_WRITE_PRINCIPAL_GROUP = "system-group";

  AuthorizationWrapper wrapper;
  PushService pushService;

  public TopicRegistry(PushService pushService) {
    this.pushService = pushService;
    wrapper = new AuthorizationWrapper();
  }

  public String createTopic(String clientId) {
    // TODO see comment about topic name on wrapper.authorize
    wrapper.authorize(clientId, clientId);

    // TODO this ensures all advisory topics are created by the system user
    pushService.pushToDummyTopic();
    return clientId;
  }

  public BrokerPlugin getAuthorizationPlugin() {
    return wrapper.getAuthorizationPlugin();
  }


  @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
  private static class AuthorizationWrapper {

    AuthorizationPlugin authorizationPlugin;
    DestinationMap readACLs;
    DestinationMap writeACLs;
    DestinationMap adminACLs;

    private AuthorizationWrapper() {
      readACLs = createTopicReadACLs();
      writeACLs = createTopicWriteACLs();
      adminACLs = createTopicAdminACLs();
      authorizationPlugin = new AuthorizationPlugin(new SimpleAuthorizationMap(writeACLs, readACLs, adminACLs));
    }

    public AuthorizationPlugin getAuthorizationPlugin() {
      return authorizationPlugin;
    }

    // TODO here we hold state that must be persisted across server restarts.
    //  maybe we can do better; instead of explicit auth map:
    //  hashing clientId and some info private to the server (ca key?)
    //  or sign the clientId with our ca and verify this here
    //  OR:
    //  why must clientId and topic be different? we control clientId anyways.
    //  just verify that clientId matches topicName. clientId is extracted from cert
    public void authorize(String clientId, String topic) {
      putTopic(readACLs, topic, clientId);
      // TODO consumer needs to be able to "auto-create" the topic and advisory
      putTopic(adminACLs, topic, clientId);
      // TODO consumers need to write to advisory topics. disable this? how to handle this?
      putTopic(adminACLs, withAdvisory(topic), clientId);
      putTopic(writeACLs, withAdvisory(topic), clientId);
    }

    private String withAdvisory(String topic) {
      return AdvisorySupport.TOPIC_CONSUMER_ADVISORY_TOPIC_PREFIX + topic;
    }

    private DestinationMap createTopicWriteACLs() {
      DestinationMap destinationMap = new DestinationMap();
      putTopic(destinationMap, "topic://>", TOPIC_WRITE_PRINCIPAL_GROUP);
      return destinationMap;
    }

    private DestinationMap createTopicReadACLs() {
      DestinationMap destinationMap = new DestinationMap();

      // allow writer reading
      putTopic(destinationMap, "topic://>", TOPIC_WRITE_PRINCIPAL_GROUP);

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
