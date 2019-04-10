package at.sbaresearch.mqtt4android.registration.crypto;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateEncodingException;

@Slf4j
@Configuration
public class CryptoConfig {

  @Bean
  public ClientKeyFactory clientKeyFactory(
      PrivateKey caKey, java.security.cert.Certificate caCert,
      @Value("${ssl.debug.writeKeysPath}") String keyPath,
      KeyWriter keyWriter
  ) throws CertificateEncodingException, OperatorCreationException, IOException {
    setupBouncyCastle();
    return new ClientKeyFactory("BC", caKey, caCert, keyPath, keyWriter);
  }

  private void setupBouncyCastle() {
    Security.addProvider(new BouncyCastleProvider());
    Security.setProperty("crypto.policy", "unlimited");
    int maxKeySize = 0;
    try {
      maxKeySize = javax.crypto.Cipher.getMaxAllowedKeyLength("AES");
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    if (maxKeySize < 2147483647) {
      throw new CryptoSetupConfigException("Max key size for AES too low: " + maxKeySize);
    }
  }

  private class CryptoSetupConfigException extends RuntimeException {
    CryptoSetupConfigException(
        String s) {
      super(s);
    }
  }
}
