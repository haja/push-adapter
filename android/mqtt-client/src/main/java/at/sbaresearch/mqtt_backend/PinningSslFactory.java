package at.sbaresearch.mqtt_backend;

import android.content.Context;
import android.util.Log;

import javax.net.SocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class PinningSslFactory {

  private static final String TAG = "PinningSslFactory";

  private final TrustManagerFactory trustManagerFactory;
  private final KeyManagerFactory keyManagerFactory;

  PinningSslFactory(Context applicationContext) throws Exception {
    Certificate ca = getCertificate(applicationContext.getResources().openRawResource(R.raw.server));
    KeyStore caKeyStore = createKeystore(ca, "ca");

    // Create a TrustManager that trusts the CAs in our KeyStore
    String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
    trustManagerFactory = TrustManagerFactory.getInstance(tmfAlgorithm);
    trustManagerFactory.init(caKeyStore);

    // TODO load client cert from memory
    Certificate clientCert = getCertificate(applicationContext.getResources().openRawResource(R.raw.client));
    KeyStore clientStore = createKeystore(clientCert, "client");
    keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
    keyManagerFactory.init(clientStore, null);
  }

  private KeyStore createKeystore(Certificate certificate, String alias)
      throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
    String keyStoreType = KeyStore.getDefaultType();
    KeyStore keyStore = KeyStore.getInstance(keyStoreType);
    keyStore.load(null, null);
    keyStore.setCertificateEntry(alias, certificate);
    return keyStore;
  }

  private Certificate getCertificate(final InputStream inputStream)
      throws CertificateException, IOException {
    CertificateFactory cf = CertificateFactory.getInstance("X.509");
    try (InputStream caInput = inputStream) {
      Certificate ca = cf.generateCertificate(caInput);
      Log.d(TAG, "getCertificate: ca=" + ((X509Certificate) ca).getSubjectDN());
      return ca;
    }
  }

  public SocketFactory getSocketFactory() throws Exception {
    SSLContext context = SSLContext.getInstance("TLS");
    context.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
    return context.getSocketFactory();
  }
}
