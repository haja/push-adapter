package at.sbaresearch.mqtt4android.common;

import lombok.val;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Component
public class SecureRngGenerator {
  private static SecureRandom RNG = new SecureRandom();

  public String randomString(int byteLength) {
    val bytes = new byte[byteLength];
    RNG.nextBytes(bytes);
    return Base64.getUrlEncoder().encodeToString(bytes)
        .replaceAll("=", "");
  }
}
