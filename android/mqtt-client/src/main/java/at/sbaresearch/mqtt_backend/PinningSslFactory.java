package at.sbaresearch.mqtt_backend;

import android.content.Context;
import android.support.v4.util.Preconditions;
import android.util.Log;

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
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Objects;

public class PinningSslFactory {

  private static final String TAG = "PinningSslFactory";

  private final TrustManagerFactory trustManagerFactory;
  private final KeyManagerFactory keyManagerFactory;

  PinningSslFactory(Context applicationContext, ConnectionSettings settings) throws Exception {
    Certificate ca =
        getCertificate(applicationContext.getResources().openRawResource(R.raw.server));
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
      Certificate cert = cf.generateCertificate(caInput);
      Log.d(TAG, "getCertificate: " + ((X509Certificate) cert).getSubjectDN());
      // TODO this returns null, which throws a NPE in org.conscrypt.KeyManagerImpl
      Log.d(TAG, "getCertificate sigAlgName: " + ((X509Certificate) cert).getSigAlgName());
      return cert;
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
