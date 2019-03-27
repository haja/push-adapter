package at.sbaresearch.mqtt_backend;

import android.content.Context;
import android.content.res.Resources;

import javax.net.SocketFactory;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class PinningSslFactory {

  private final TrustManagerFactory trustManagerFactory;

  PinningSslFactory(Context applicationContext) throws Exception {
    // Load CAs from an InputStream
    // (could be from a resource or ByteArrayInputStream or ...)
    CertificateFactory cf = CertificateFactory.getInstance("X.509");
    InputStream caInput = applicationContext.getResources().openRawResource(R.raw.server);
    Certificate ca;
    try {
      ca = cf.generateCertificate(caInput);
      System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
    } finally {
      caInput.close();
    }

    // Create a KeyStore containing our trusted CAs
    String keyStoreType = KeyStore.getDefaultType();
    KeyStore keyStore = KeyStore.getInstance(keyStoreType);
    keyStore.load(null, null);
    keyStore.setCertificateEntry("ca", ca);

    // Create a TrustManager that trusts the CAs in our KeyStore
    String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
    trustManagerFactory = TrustManagerFactory.getInstance(tmfAlgorithm);
    trustManagerFactory.init(keyStore);
  }

  public SocketFactory getSocketFactory() throws Exception {
    SSLContext context = SSLContext.getInstance("TLS");
    context.init(null, trustManagerFactory.getTrustManagers(), null);
    return context.getSocketFactory();
  }
}
