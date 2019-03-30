package at.sbaresearch.mqtt4android.relay.jaas;

import org.apache.activemq.jaas.CertificateLoginModule;

import javax.security.auth.login.LoginException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Set;

public class SimpleCertificateLoginModule extends CertificateLoginModule {

  private static final String TOPIC_READ_PRINCIPAL_GROUP = "read-group";

  @Override
  protected String getUserNameForCertificates(X509Certificate[] certs) throws LoginException {
    if (certs == null) {
      throw new LoginException("Client certificates not found. Cannot authenticate.");
    }
    // TODO verify certificate against our trustStore? do we rely that the ssl cert must already be authenticated since we accepted the ssl connection?
    return getDistinguishedName(certs);
  }

  @Override
  protected Set<String> getUserGroups(String username) throws LoginException {
    // TODO mocked group for now
    return Collections.singleton(TOPIC_READ_PRINCIPAL_GROUP);
    //return Collections.singleton(username);
  }
}
