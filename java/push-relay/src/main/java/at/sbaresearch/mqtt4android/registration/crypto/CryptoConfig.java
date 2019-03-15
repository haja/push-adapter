package at.sbaresearch.mqtt4android.registration.crypto;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

@Configuration
@Slf4j
public class CryptoConfig {

  @PostConstruct
  public void init() {
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
