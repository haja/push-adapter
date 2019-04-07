package at.sbaresearch.mqtt4android.pinning;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.val;

import javax.net.SocketFactory;
import javax.net.ssl.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PinningSslFactory {

  @Getter
  SSLContext sslContext;
  X509TrustManager[] trustManagers;


  /**
   * ssl factory with pinning and client keys
   */
  public PinningSslFactory(ClientKeyCert settings, InputStream caStream) throws Exception {
    trustManagers = setupTrust(caStream);
    val keyManager = setupClientKeys(settings);

    sslContext = SSLContext.getInstance("TLS");
    sslContext.init(keyManager, trustManagers, null);
  }

  /**
   * ssl factory with pinning, without client keys
   */
  public PinningSslFactory(InputStream caStream) throws Exception {
    trustManagers = setupTrust(caStream);

    sslContext = SSLContext.getInstance("TLS");
    sslContext.init(null, trustManagers, null);
  }

  private KeyManager[] setupClientKeys(ClientKeyCert settings)
      throws NoSuchAlgorithmException, InvalidKeySpecException, CertificateException, IOException,
             KeyStoreException, UnrecoverableKeyException {
    val clientKey = getKey(settings.getPrivKey());
    Certificate[] clientCert = new Certificate[]{
        getCertificate(settings.getCert())
    };
    val clientStore = from(clientKey, "client", clientCert);
    val keyManagerFactory =
        KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
    keyManagerFactory.init(clientStore, null);
    return keyManagerFactory.getKeyManagers();
  }

  private X509TrustManager[] setupTrust(InputStream caStream)
      throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException {
    val ca = getCertificate(caStream);
    val caKeyStore = from(ca, "ca");

    // Create a TrustManager that trusts the CAs in our KeyStore
    val defaultAlg = TrustManagerFactory.getDefaultAlgorithm();
    val trustManagerFactory = TrustManagerFactory.getInstance(defaultAlg);
    trustManagerFactory.init(caKeyStore);
    return toX509(trustManagerFactory.getTrustManagers());
  }

  private X509TrustManager[] toX509(TrustManager[] trustManagers) {
    val ret = new ArrayList<X509TrustManager>();
    for (TrustManager tm : trustManagers) {
      ret.add((X509TrustManager) tm);
    }
    return ret.toArray(new X509TrustManager[1]);
  }

  private Certificate getCertificate(byte[] cert) throws CertificateException, IOException {
    return getCertificate(new ByteArrayInputStream(cert));
  }

  private Certificate getCertificate(final InputStream inputStream)
      throws CertificateException, IOException {
    CertificateFactory cf = CertificateFactory.getInstance("X.509");
    try (InputStream caInput = inputStream) {
      return cf.generateCertificate(caInput);
    }
  }

  private KeyStore from(Certificate certificate, String alias)
      throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
    KeyStore keyStore = createKeyStore();
    keyStore.setCertificateEntry(alias, certificate);
    return keyStore;
  }

  private Key getKey(byte[] encodedKey)
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    KeyFactory kf = KeyFactory.getInstance("EC");
    KeySpec spec = new PKCS8EncodedKeySpec(encodedKey);
    return kf.generatePrivate(spec);
  }

  private KeyStore from(Key key, String alias, Certificate[] chain)
      throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
    Objects.requireNonNull(key, "key null");
    Objects.requireNonNull(chain, "chain null");
    KeyStore keyStore = createKeyStore();
    keyStore.setKeyEntry(alias, key, null, chain);
    return keyStore;
  }

  private KeyStore createKeyStore()
      throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
    val keyStoreType = KeyStore.getDefaultType();
    val keyStore = KeyStore.getInstance(keyStoreType);
    keyStore.load(null, null);
    return keyStore;
  }

  public SSLSocketFactory getSocketFactory() throws Exception {
    return sslContext.getSocketFactory();
  }

  public X509TrustManager getTrustManager() {
    return trustManagers[0];
  }

}
