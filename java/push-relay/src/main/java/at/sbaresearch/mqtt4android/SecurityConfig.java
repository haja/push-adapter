package at.sbaresearch.mqtt4android;

import at.sbaresearch.mqtt4android.SecurityConfig.SslConfig;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.net.URL;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;

@Configuration
@EnableConfigurationProperties(SslConfig.class)
public class SecurityConfig {

  @Bean(name = "keyStore")
  public KeyStore keyStore(SslConfig ssl) throws Exception {
    return getKeyStore(ssl);
  }

  @Bean(name = "trustStore")
  public KeyStore trustStore(SslConfig ssl) throws Exception {
    return getTrustStore(ssl);
  }

  @Bean
  public PrivateKey caKey(@Qualifier("trustStore") KeyStore trustStore, SslConfig ssl) throws Exception {
    val pwd = ssl.trustStorePassword;
    val pwdAsChar = pwd == null ? null : pwd.toCharArray();
    return (PrivateKey) trustStore.getKey(ssl.caKeyAlias, pwdAsChar);
  }

  @Bean(name = "caCert")
  public Certificate caCert(@Qualifier("trustStore") KeyStore trustStore, SslConfig ssl) throws Exception {
    return trustStore.getCertificate(ssl.caKeyAlias);
  }

  @Bean
  public KeyManager[] keyManager(@Qualifier("keyStore") KeyStore keyStore, SslConfig ssl) {
    return getKeyManagerFactory(keyStore, ssl).getKeyManagers();
  }

  @Bean
  public TrustManager[] trustManager(@Qualifier("trustStore") KeyStore trustStore) {
    return getTrustManagerFactory(trustStore).getTrustManagers();
  }

  private KeyManagerFactory getKeyManagerFactory(KeyStore keyStore, SslConfig ssl) {
    try {
      KeyManagerFactory keyManagerFactory = KeyManagerFactory
          .getInstance(KeyManagerFactory.getDefaultAlgorithm());
      char[] keyPassword = (ssl.getKeyPassword() != null)
          ? ssl.getKeyPassword().toCharArray() : null;
      if (keyPassword == null && ssl.getKeyStorePassword() != null) {
        keyPassword = ssl.getKeyStorePassword().toCharArray();
      }
      keyManagerFactory.init(keyStore, keyPassword);
      return keyManagerFactory;
    } catch (Exception ex) {
      throw new SecurityConfigException(ex);
    }
  }

  private KeyStore getKeyStore(SslConfig ssl)
      throws Exception {
    return loadKeyStore(ssl.getKeyStoreProvider(),
        ssl.getKeyStore(), ssl.getKeyStorePassword(), ssl.getKeyStoreType());
  }

  private TrustManagerFactory getTrustManagerFactory(KeyStore store) {
    try {
      TrustManagerFactory trustManagerFactory = TrustManagerFactory
          .getInstance(TrustManagerFactory.getDefaultAlgorithm());
      trustManagerFactory.init(store);
      return trustManagerFactory;
    } catch (Exception ex) {
      throw new SecurityConfigException(ex);
    }
  }

  private KeyStore getTrustStore(SslConfig ssl)
      throws Exception {
    return loadKeyStore(ssl.getTrustStoreProvider(),
        ssl.getTrustStore(), ssl.getTrustStorePassword(), ssl.getTrustStoreType());
  }

  private KeyStore loadKeyStore(String provider, String resource, String password, String type)
      throws Exception {
    if (resource == null) {
      throw new SecurityConfigException("keyStore resource is null");
    }
    KeyStore store = (provider != null) ? KeyStore.getInstance(type, provider)
        : KeyStore.getInstance(type);
    URL url = ResourceUtils.getURL(resource);
    store.load(url.openStream(), (password != null) ? password.toCharArray() : null);
    return store;
  }

  @ConfigurationProperties("ssl")
  @Getter
  @Setter
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class SslConfig {
    String keyStore;
    String keyStorePassword;
    String keyStoreProvider;
    String keyStoreType;
    String serverCertAlias;

    String caKeyAlias;
    String keyPassword;

    String trustStore;
    String trustStorePassword;
    String trustStoreProvider;
    String trustStoreType;
  }

  private class SecurityConfigException extends RuntimeException {
    public SecurityConfigException(String msg) {
      super(msg);
    }

    public SecurityConfigException(Exception ex) {
      super(ex);
    }
  }
}
