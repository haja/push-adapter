package at.sbaresearch.mqtt4android.pinning;

import javax.net.SocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
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
import java.util.Objects;

// TODO how to annotate that this is "Public API"?
public class PinningSslFactory {

  private final TrustManagerFactory trustManagerFactory;
  private final KeyManagerFactory keyManagerFactory;

  public PinningSslFactory(ConnectionSettings settings, InputStream caStream) throws Exception {
    Certificate ca = getCertificate(caStream);
    KeyStore caKeyStore = from(ca, "ca");

    // Create a TrustManager that trusts the CAs in our KeyStore
    String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
    trustManagerFactory = TrustManagerFactory.getInstance(tmfAlgorithm);
    trustManagerFactory.init(caKeyStore);

    Key clientKey = getKey(settings.getPrivKey());
    Certificate[] clientCert = new Certificate[]{
        getCertificate(settings.getCert())
    };
    KeyStore clientStore = from(clientKey, "client", clientCert);
    keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
    keyManagerFactory.init(clientStore, null);
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
    String keyStoreType = KeyStore.getDefaultType();
    KeyStore keyStore = KeyStore.getInstance(keyStoreType);
    keyStore.load(null, null);
    return keyStore;
  }

  public SocketFactory getSocketFactory() throws Exception {
    SSLContext context = SSLContext.getInstance("TLS");
    context.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
    return context.getSocketFactory();
  }
}
