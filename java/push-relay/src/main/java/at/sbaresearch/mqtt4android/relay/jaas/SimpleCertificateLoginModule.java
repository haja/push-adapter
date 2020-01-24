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

import at.sbaresearch.mqtt4android.relay.TopicRegistry;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.activemq.jaas.CertificateLoginModule;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;

import javax.security.auth.login.LoginException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class SimpleCertificateLoginModule extends CertificateLoginModule {

  @Override
  protected String getUserNameForCertificates(X509Certificate[] certs) throws LoginException {
    if (certs == null) {
      throw new LoginException("Client certificates not found. Cannot authenticate.");
    }
    // TODO verify certificate against our trustStore? do we rely that the ssl cert must already be authenticated since we accepted the ssl connection?
    try {
      val username = getCn(certs);
      log.info("got username: {}", username);
      return username;
    } catch (CertificateEncodingException e) {
      throw new LoginException("cannot extract CN " + e.getMessage());
    }
  }

  private String getCn(X509Certificate[] certs) throws CertificateEncodingException {
    if (certs != null && certs.length > 0 && certs[0] != null) {
      val subject = new JcaX509CertificateHolder(certs[0]).getSubject();
      val cn = subject.getRDNs(BCStyle.CN)[0];
      return IETFUtils.valueToString(cn.getFirst().getValue());
    } else {
      return null;
    }
  }

  @Override
  protected Set<String> getUserGroups(String username) throws LoginException {
    log.info("got userGroups for username: {}", username);
    return new HashSet<>(Arrays.asList(username, TopicRegistry.TOPIC_PRINCIPAL_READER));
  }
}
