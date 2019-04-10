package at.sbaresearch.mqtt4android.registration.crypto;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

@Component
@Slf4j
public class KeyWriter {
  public void write(final byte[] privateKey, final byte[] cert, File path)
      throws IOException, CertificateException {
    log.warn("writing key+certificate to filesystem at {}", path.getAbsolutePath());

    val serial = ((X509Certificate) CertificateFactory.getInstance("X.509")
        .generateCertificate(new ByteArrayInputStream(cert)))
        .getSerialNumber();

    val certPath = new File(path, "cert-" + serial);
    try (val fos = new FileOutputStream(certPath)) {
      fos.write(cert);
    }

    val keyPath = new File(path, "key-" + serial);
    try (val fos = new FileOutputStream(keyPath)) {
      fos.write(privateKey);
    }
  }
}
