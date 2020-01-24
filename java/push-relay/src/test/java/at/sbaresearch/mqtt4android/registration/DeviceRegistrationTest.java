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

import at.sbaresearch.mqtt4android.AppTest;
import at.sbaresearch.mqtt4android.registration.web.RegistrationResource;
import lombok.val;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static at.sbaresearch.mqtt4android.registration.RegistrationTestHelper.deviceReq;
import static org.assertj.core.api.Assertions.*;

public class DeviceRegistrationTest extends AppTest {

  @Autowired
  RegistrationResource registrationResource;

  @Test
  public void testRegistration_shouldBeNotNull() throws Exception {
    val reg = registrationResource.registerDevice(deviceReq().build());
    assertThat(reg).isNotNull();
    assertThat(reg).hasNoNullFieldsOrProperties();
  }

  @Test
  public void testRegistrationTwice_shouldReturnDifferentTopicAndCredentials() throws Exception {
    val reg = registrationResource.registerDevice(deviceReq().build());
    val reg2 = registrationResource.registerDevice(deviceReq().build());

    SoftAssertions.assertSoftly(softly -> {
      softly.assertThat(reg.getMqttTopic()).isNotEqualToIgnoringCase(reg2.getMqttTopic());
      softly.assertThat(reg.getEncodedCert()).isNotEqualTo(reg2.getEncodedCert());
      softly.assertThat(reg.getEncodedPrivateKey()).isNotEqualTo(reg2.getEncodedPrivateKey());
    });
  }
}
