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
