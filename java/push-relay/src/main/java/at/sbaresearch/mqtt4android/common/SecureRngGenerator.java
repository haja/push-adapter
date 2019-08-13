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
      System.out.println("starting static block");
      RNG = SecureRandom.getInstanceStrong();
      System.out.println("after static block");
    } catch (NoSuchAlgorithmException e) {
      System.err.println("cannot get SecureRandom: " + e.getMessage());
      throw new RuntimeException(e);
    }
  }

  public String randomString(int byteLength) {
    val bytes = new byte[byteLength];
    RNG.nextBytes(bytes);
    return Base64.getUrlEncoder().encodeToString(bytes)
        .replaceAll("=", "");
  }
}
