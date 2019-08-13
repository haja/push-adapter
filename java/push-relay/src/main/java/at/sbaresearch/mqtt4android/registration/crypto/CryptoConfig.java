package at.sbaresearch.mqtt4android.registration.crypto;

import at.sbaresearch.mqtt4android.common.SecureRngGenerator;
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
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;

@Slf4j
@Configuration
public class CryptoConfig {

  @Bean
  public ClientKeyFactory clientKeyFactory(
      PrivateKey caKey, Certificate caCert,
      @Value("${ssl.debug.writeKeysPath}") String keyPath,
      KeyWriter keyWriter,
      SerialDao serialDao, SecureRngGenerator rng) throws CertificateEncodingException, OperatorCreationException, IOException {
    setupBouncyCastle();
    return new ClientKeyFactory("BC", caKey, caCert, keyPath, keyWriter, serialDao, rng);
  }

  private void setupBouncyCastle() {
    Security.addProvider(new BouncyCastleProvider());
    Security.setProperty("crypto.policy", "unlimited");
    int maxKeySize = 0;
    try {
      maxKeySize = javax.crypto.Cipher.getMaxAllowedKeyLength("AES");
    } catch (NoSuchAlgorithmException e) {
      log.error("could not get algorithm", e);
    }
    if (maxKeySize < 2147483647) {
      String msg = "Max key size for AES too low: " + maxKeySize;
      log.error(msg);
      throw new CryptoSetupConfigException(msg);
    }
  }

  private class CryptoSetupConfigException extends RuntimeException {
    CryptoSetupConfigException(
        String s) {
      super(s);
    }
  }
}
