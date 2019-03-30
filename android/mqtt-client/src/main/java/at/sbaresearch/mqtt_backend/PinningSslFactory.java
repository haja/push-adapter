package at.sbaresearch.mqtt_backend;

import android.content.Context;
import android.util.Log;

import javax.net.SocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.*;

public class PinningSslFactory {

  private static final String TAG = "PinningSslFactory";

  private final TrustManagerFactory trustManagerFactory;
  private final KeyManagerFactory keyManagerFactory;

  PinningSslFactory(Context applicationContext) throws Exception {
    Certificate ca = getCertificate(applicationContext.getResources().openRawResource(R.raw.server));
    KeyStore caKeyStore = from(ca, "ca");

    // Create a TrustManager that trusts the CAs in our KeyStore
    String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
    trustManagerFactory = TrustManagerFactory.getInstance(tmfAlgorithm);
    trustManagerFactory.init(caKeyStore);

    // TODO load client key from memory
    Key clientKey = getKey(applicationContext.getResources().openRawResource(R.raw.client_key));
    Certificate[] clientCert = new Certificate[] {
        getCertificate(applicationContext.getResources().openRawResource(R.raw.client_cert))
    };
    KeyStore clientStore = from(clientKey, "client", clientCert);
    keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
    keyManagerFactory.init(clientStore, null);
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

  private KeyStore from(Certificate certificate, String alias)
      throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
    KeyStore keyStore = createKeyStore();
    keyStore.setCertificateEntry(alias, certificate);
    return keyStore;
  }

  private Key getKey(InputStream inputStream)
      throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
    KeyFactory kf = KeyFactory.getInstance("EC");
    try (InputStream in = inputStream) {
      byte[] bytes = toByteArray(in);
      KeySpec spec = new PKCS8EncodedKeySpec(bytes);
      return kf.generatePrivate(spec);
    }
  }

  private byte[] toByteArray(InputStream in) throws IOException {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    while (in.available() > 0) {
      bos.write(in.read());
    }
    return bos.toByteArray();
  }

  private KeyStore from(Key key, String alias, Certificate[] chain)
      throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
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
