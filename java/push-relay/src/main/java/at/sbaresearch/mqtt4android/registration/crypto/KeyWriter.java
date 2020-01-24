/*
 * Copyright (c) 2020 Harald Jagenteufel.
 *
 * This file is part of push-relay.
 *
 *     push-relay is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     push-relay is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with push-relay.  If not, see <https://www.gnu.org/licenses/>.
 */

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
