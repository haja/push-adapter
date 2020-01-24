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
