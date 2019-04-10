package at.sbaresearch.mqtt4android.registration.crypto;

import at.sbaresearch.mqtt4android.registration.crypto.ClientKeyFactory.ClientKeys;
import at.sbaresearch.mqtt4android.TestData;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.boot.CommandLineRunner;

import java.io.File;

@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CryptoTestSetupHelper implements CommandLineRunner {

  ClientKeyFactory keyFactory;
  KeyWriter keyWriter;
  File outputPath;
  TestData testData;

  public CryptoTestSetupHelper(ClientKeyFactory keyFactory, KeyWriter keyWriter,
      String outputPath, TestData testData) {
    this.keyFactory = keyFactory;
    this.keyWriter = keyWriter;
    this.outputPath = new File(outputPath);
    this.testData = testData;
  }

  @Override
  public void run(String... args) throws Exception {
    log.info("generating keys for registrations");
    val reg1 = testData.registrations.registration1;
    val key1 = generateSignedKeyForTest(reg1.getTopic());
    keyWriter.write(key1.getEncodedPrivateKey(), key1.getEncodedCert(), outputPath
    );
  }

  private ClientKeys generateSignedKeyForTest(String clientId) throws Exception {
    return keyFactory.createSignedKey(clientId);
  }
}
