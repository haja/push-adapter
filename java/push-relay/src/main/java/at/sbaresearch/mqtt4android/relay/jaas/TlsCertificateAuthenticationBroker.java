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

package at.sbaresearch.mqtt4android.relay.jaas;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.security.JaasCertificateAuthenticationBroker;

public class TlsCertificateAuthenticationBroker extends JaasCertificateAuthenticationBroker {
  public TlsCertificateAuthenticationBroker(Broker broker, String configuration) {
    super(broker, configuration);
  }
}
