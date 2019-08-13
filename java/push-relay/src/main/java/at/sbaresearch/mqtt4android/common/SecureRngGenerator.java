package at.sbaresearch.mqtt4android.common;

import lombok.val;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class SecureRngGenerator {
  private final static SecureRandom RNG;

  static {
    try {
      RNG = SecureRandom.getInstanceStrong();
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  public String randomString(int byteLength) {
    val bytes = new byte[byteLength];
    RNG.nextBytes(bytes);
    return Base64.getUrlEncoder().encodeToString(bytes)
        .replaceAll("=", "");
  }

  public SecureRandom getSecureRandom() {
    return RNG;
  }
}
