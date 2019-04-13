package at.sbaresearch.mqtt4android.registration.crypto;

import at.sbaresearch.mqtt4android.registration.crypto.ClientKeyFactory.ClientKeys;
import at.sbaresearch.mqtt4android.testdata.Registrations;
import at.sbaresearch.mqtt4android.testdata.Registrations.RegistrationRecord;
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
  Registrations registrations;

  public CryptoTestSetupHelper(ClientKeyFactory keyFactory, KeyWriter keyWriter,
      String outputPath, Registrations registrations) {
    this.keyFactory = keyFactory;
    this.keyWriter = keyWriter;
    this.outputPath = new File(outputPath);
    if (!this.outputPath.isDirectory() || !this.outputPath.canWrite()) {
      throw new RuntimeException("cannot write to directory '" + this.outputPath.getAbsolutePath() + "'. are you running from the right working directory?");
    }
    this.registrations = registrations;
  }

  @Override
  public void run(String... args) throws Exception {
    log.info("generating keys for registrations");
    generateAndWrite(registrations.registration1);
    generateAndWrite(registrations.registration2);
  }

  private void generateAndWrite(RegistrationRecord reg) throws Exception {
    val key = generateSignedKeyForTest(reg.getTopic());
    keyWriter.write(key.getEncodedPrivateKey(), key.getEncodedCert(), outputPath
    );
  }

  private ClientKeys generateSignedKeyForTest(String clientId) throws Exception {
    return keyFactory.createSignedKey(clientId);
  }
}
