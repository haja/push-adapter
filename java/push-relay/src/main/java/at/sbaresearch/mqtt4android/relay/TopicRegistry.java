package at.sbaresearch.mqtt4android.relay;

import at.sbaresearch.mqtt4android.registration.crypto.ClientKeyFactory.ClientKeys;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.apache.activemq.advisory.AdvisorySupport;
import org.apache.activemq.broker.BrokerPlugin;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.filter.DestinationMap;
import org.apache.activemq.jaas.GroupPrincipal;
import org.apache.activemq.security.AuthorizationPlugin;
import org.apache.activemq.security.SimpleAuthorizationMap;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TopicRegistry {
  public static final String TOPIC_WRITE_PRINCIPAL_GROUP = "system-group";
  private static final String TOPIC_READ_PRINCIPAL_GROUP = "read-group";

  AuthorizationWrapper wrapper;
  PushService pushService;

  public TopicRegistry(PushService pushService) {
    this.pushService = pushService;
    wrapper = new AuthorizationWrapper();
  }

  public String createTopic(ClientKeys cert) {
    val topicName = MqttBrokerConfig.MQTT_MOCK_TOPIC;
    ActiveMQTopic topic = new ActiveMQTopic(topicName);

    // TODO create mqtt topic and authorize clientCert for this topic
    //  for now, this creates the mocked topic
    pushService.pushMessage("hello");

    //  return clientCert + connection settings + topic
    return topicName;
  }

  public BrokerPlugin getAuthorizationPlugin() {
    return wrapper.getAuthorizationPlugin();
  }


  @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
  private static class AuthorizationWrapper {

    AuthorizationPlugin authorizationPlugin;

    private AuthorizationWrapper() {
      // TODO fix ACLs
      DestinationMap writeACLs = getTopicWriteACLs();
      DestinationMap readACLs = getTopicReadACLs();
      authorizationPlugin = new AuthorizationPlugin(new SimpleAuthorizationMap(writeACLs, readACLs, writeACLs));
    }

    private AuthorizationPlugin getAuthorizationPlugin() {
      return authorizationPlugin;
    }

    private DestinationMap getTopicWriteACLs() {
      DestinationMap destinationMap = new DestinationMap();
      putTopic(destinationMap, "topic://>", TOPIC_WRITE_PRINCIPAL_GROUP);

      // TODO consumers need to write to advisory topics. disable this? how to handle this?
      putTopic(destinationMap,
          AdvisorySupport.TOPIC_CONSUMER_ADVISORY_TOPIC_PREFIX + "*",
          TOPIC_READ_PRINCIPAL_GROUP);
      return destinationMap;
    }

    private DestinationMap getTopicReadACLs() {
      DestinationMap destinationMap = new DestinationMap();

      // allow writer reading
      putTopic(destinationMap, "topic://>", TOPIC_WRITE_PRINCIPAL_GROUP);

      putTopic(destinationMap, "topic://foo", TOPIC_READ_PRINCIPAL_GROUP);
      return destinationMap;
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
