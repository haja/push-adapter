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
    return getStore(ssl.key);
  }

  @Bean(name = "trustStore")
  public KeyStore trustStore(SslConfig ssl) throws Exception {
    return getStore(ssl.trust);
  }

  @Bean(name = "caStore")
  public KeyStore caStore(SslConfig ssl) throws Exception {
    return getStore(ssl.ca);
  }

  @Bean
  public PrivateKey caKey(@Qualifier("caStore") KeyStore caStore, SslConfig ssl) throws Exception {
    val pwd = ssl.ca.storePassword;
    val pwdAsChar = pwd == null ? null : pwd.toCharArray();
    return (PrivateKey) caStore.getKey(ssl.caKeyAlias, pwdAsChar);
  }

  @Bean(name = "caCert")
  public Certificate caCert(@Qualifier("caStore") KeyStore caStore, SslConfig ssl) throws Exception {
    return caStore.getCertificate(ssl.caKeyAlias);
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
      if (keyPassword == null && ssl.key.getStorePassword() != null) {
        keyPassword = ssl.key.getStorePassword().toCharArray();
      }
      keyManagerFactory.init(keyStore, keyPassword);
      return keyManagerFactory;
    } catch (Exception ex) {
      throw new SecurityConfigException(ex);
    }
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

  private KeyStore getStore(StoreConfig cfg) throws Exception {
    return loadKeyStore(cfg.getStoreProvider(),
        cfg.getStore(), cfg.getStorePassword(), cfg.getStoreType());
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
    StoreConfig key;
    String serverCertAlias;
    String keyPassword;

    StoreConfig trust;

    StoreConfig ca;
    String caKeyAlias;
  }

  @Getter
  @Setter
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class StoreConfig {
    String store;
    String storePassword;
    String storeProvider;
    String storeType;
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
