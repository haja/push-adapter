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

package at.sbaresearch.mqtt4android;

import at.sbaresearch.mqtt4android.registration.crypto.ClientKeyFactory;
import at.sbaresearch.mqtt4android.registration.crypto.CryptoTestSetupHelper;
import at.sbaresearch.mqtt4android.registration.crypto.KeyWriter;
import at.sbaresearch.mqtt4android.testdata.Registrations;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.*;
import org.springframework.web.bind.annotation.RestController;

/**
 * run this application to handles one-time setup of testdata
 */
@Profile("test-setup")
@Configuration
@EnableAutoConfiguration(exclude = {
    WebMvcAutoConfiguration.class,
})
@ComponentScan(basePackageClasses = PushRelayApplication.class,
    excludeFilters = {
    @ComponentScan.Filter(type = FilterType.ANNOTATION, value = RestController.class)
    })
public class OneTimeTestSetupHelper {

  public static void main(String[] args) throws Exception {
    val app = new SpringApplication(OneTimeTestSetupHelper.class);
    app.setAdditionalProfiles("it", "test-setup");
    app.run(args);
  }

  @Bean
  public CryptoTestSetupHelper cryptoTestSetupHelper(
      ClientKeyFactory keyFactory,
      KeyWriter keyWriter,
      @Value("${testSetup.ssl.clientKeysPathForGenerate}") String testKeyPath,
      Registrations registrations) {
    return new CryptoTestSetupHelper(keyFactory, keyWriter, testKeyPath, registrations);
  }
}
