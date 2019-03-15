package at.sbaresearch.mqtt4android;

import at.sbaresearch.mqtt4android.SecurityConfig.SslConfig;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.val;
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

@Configuration
@EnableConfigurationProperties(SslConfig.class)
public class SecurityConfig {

  @Bean
  public KeyManager[] keyManager(SslConfig ssl) {
    return getKeyManagerFactory(ssl).getKeyManagers();
  }

  @Bean
  public TrustManager[] trustManager(SslConfig ssl) {
    return getTrustManagerFactory(ssl).getTrustManagers();
  }

  private KeyManagerFactory getKeyManagerFactory(SslConfig ssl) {
    try {
      KeyStore keyStore = getKeyStore(ssl);
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
        ssl.getKeyStore(), ssl.getKeyStorePassword());
  }

  private TrustManagerFactory getTrustManagerFactory(SslConfig ssl) {
    try {
      KeyStore store = getTrustStore(ssl);
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
        ssl.getTrustStore(), ssl.getTrustStorePassword());
  }

  private KeyStore loadKeyStore(String provider, String resource, String password)
      throws Exception {
    if (resource == null) {
      throw new SecurityConfigException("keyStore resource is null");
    }
    val type = "JKS";
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

    String keyPassword;

    String trustStore;
    String trustStorePassword;
    String trustStoreProvider;
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
