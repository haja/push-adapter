package at.sbaresearch.mqtt4android;

import at.sbaresearch.mqtt4android.SecurityConfig.SslConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.security.KeyStore;
import java.security.cert.Certificate;

@Profile("it")
@Configuration
public class ItConfig {

  @Bean(name = "serverCert")
  public Certificate serverCert(@Qualifier("keyStore") KeyStore keyStore, SslConfig ssl) throws Exception {
    return keyStore.getCertificate(ssl.getServerCertAlias());
  }
}
