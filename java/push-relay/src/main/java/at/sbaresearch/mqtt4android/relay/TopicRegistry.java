/*
 * Copyright (c) 2020 Harald Jagenteufel.
 *
 * This file is part of push-relay.
 *
 *     push-relay is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     push-relay is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with push-relay.  If not, see <https://www.gnu.org/licenses/>.
 */

package at.sbaresearch.mqtt4android.relay;

import at.sbaresearch.mqtt4android.relay.mqtt.CustomAuthorizationPlugin;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.activemq.broker.BrokerPlugin;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.filter.DestinationMap;
import org.apache.activemq.jaas.GroupPrincipal;
import org.apache.activemq.security.SimpleAuthorizationMap;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TopicRegistry {
  public static final String TOPIC_PRINCIPAL_WRITER = "system-group";
  public static final String TOPIC_PRINCIPAL_READER = "client-group";

  AuthorizationWrapper wrapper;

  public TopicRegistry() {
    wrapper = new AuthorizationWrapper();
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
      putTopic(destinationMap, "topic://>", TOPIC_PRINCIPAL_WRITER);
      // allow full access to advisory topics, see http://activemq.apache.org/security
      putTopic(destinationMap, "topic://ActiveMQ.Advisory.>", TOPIC_PRINCIPAL_READER);
      return destinationMap;
    }

    private DestinationMap createTopicReadACLs() {
      return createTopicWriteACLs();
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
