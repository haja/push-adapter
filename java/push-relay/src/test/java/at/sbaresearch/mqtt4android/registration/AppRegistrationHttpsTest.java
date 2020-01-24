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

package at.sbaresearch.mqtt4android.registration;

import at.sbaresearch.mqtt4android.HttpTest;
import at.sbaresearch.mqtt4android.TestData;
import at.sbaresearch.mqtt4android.registration.crypto.CryptoTestHelper;
import at.sbaresearch.mqtt4android.registration.web.RegistrationResource;
import at.sbaresearch.mqtt4android.registration.web.RegistrationResource.AppRegistrationResponse;
import lombok.val;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.*;

public class AppRegistrationHttpsTest extends HttpTest {
  @Autowired
  private TestData testData;
  @Autowired
  private CryptoTestHelper cryptoHelper;

  @Test
  public void testAppRegistration_shouldSucceed() throws Exception {
    val req = RegistrationResource.AppRegistrationRequest.testWith()
        .app("my.test.app")
        .senderId("333")
        .build();

    val restTemplate = cryptoHelper.addClientKeys(this.restBuilder, testData.clients.client1Keys)
        .build();

    assertThat(restTemplate.postForObject(RegistrationResource.REGISTRATION_APP,
        req, AppRegistrationResponse.class).getToken())
        .isNotEmpty();
  }
}
