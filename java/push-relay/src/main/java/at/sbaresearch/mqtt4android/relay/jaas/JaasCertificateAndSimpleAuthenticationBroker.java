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

import org.apache.activemq.broker.*;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ConnectionInfo;
import org.apache.activemq.security.*;

import java.security.cert.X509Certificate;

public class JaasCertificateAndSimpleAuthenticationBroker
    extends BrokerFilter implements AuthenticationBroker {
  private final TlsCertificateAuthenticationBroker sslBroker;
  private final AbstractAuthenticationBroker nonSslBroker;

  /**
   * @param next The Broker that does the actual work for this Filter.
   * @param jaasSslConfiguration The JAAS domain configuration name for
   * SSL connections (refer to JAAS documentation).
   */
  public JaasCertificateAndSimpleAuthenticationBroker(Broker next, String jaasSslConfiguration, AbstractAuthenticationBroker nonSslBroker) {
    super(next);

    this.nonSslBroker = nonSslBroker;
    this.sslBroker = new TlsCertificateAuthenticationBroker(
        new EmptyBroker(), jaasSslConfiguration);
  }

  /**
   * Overridden to allow for authentication using different Jaas
   * configurations depending on if the connection is SSL or not.
   */
  @Override
  public void addConnection(ConnectionContext context, ConnectionInfo info) throws Exception {
    if (context.getSecurityContext() == null) {
      if (isSSL(context, info)) {
        this.sslBroker.addConnection(context, info);
      } else {
        this.nonSslBroker.addConnection(context, info);
      }
      super.addConnection(context, info);
    }
  }

  /**
   * Overriding removeConnection to make sure the security context is cleaned.
   */
  @Override
  public void removeConnection(ConnectionContext context, ConnectionInfo info, Throwable error)
      throws Exception {
    super.removeConnection(context, info, error);
    if (isSSL(context, info)) {
      this.sslBroker.removeConnection(context, info, error);
    } else {
      this.nonSslBroker.removeConnection(context, info, error);
    }
  }

  private boolean isSSL(ConnectionContext context, ConnectionInfo info) throws Exception {
    boolean sslCapable = false;
    Connector connector = context.getConnector();
    if (connector instanceof TransportConnector) {
      TransportConnector transportConnector = (TransportConnector) connector;
      sslCapable = transportConnector.getServer().isSslServer();
    }
    // AMQ-5943, also check if transport context carries X509 cert
    if (!sslCapable && info.getTransportContext() instanceof X509Certificate[]) {
      sslCapable = true;
    }
    return sslCapable;
  }

  @Override
  public void removeDestination(ConnectionContext context, ActiveMQDestination destination,
      long timeout) throws Exception {
    // Give both a chance to clear out their contexts
    this.sslBroker.removeDestination(context, destination, timeout);
    this.nonSslBroker.removeDestination(context, destination, timeout);

    super.removeDestination(context, destination, timeout);
  }

  @Override
  public SecurityContext authenticate(String username, String password,
      X509Certificate[] peerCertificates) throws SecurityException {
    if (peerCertificates != null) {
      return this.sslBroker.authenticate(username, password, peerCertificates);
    } else {
      return this.nonSslBroker.authenticate(username, password, peerCertificates);
    }
  }
}
