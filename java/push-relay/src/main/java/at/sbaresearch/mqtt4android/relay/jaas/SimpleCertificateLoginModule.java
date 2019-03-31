package at.sbaresearch.mqtt4android.relay.jaas;

import lombok.val;
import org.apache.activemq.jaas.CertificateLoginModule;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;

import javax.security.auth.login.LoginException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Set;

public class SimpleCertificateLoginModule extends CertificateLoginModule {

  @Override
  protected String getUserNameForCertificates(X509Certificate[] certs) throws LoginException {
    if (certs == null) {
      throw new LoginException("Client certificates not found. Cannot authenticate.");
    }
    // TODO verify certificate against our trustStore? do we rely that the ssl cert must already be authenticated since we accepted the ssl connection?
    try {
      return getCn(certs);
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
    return Collections.singleton(username);
  }
}
