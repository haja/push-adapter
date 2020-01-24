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

package at.sbaresearch.mqtt4android.integration;

import at.sbaresearch.mqtt4android.AppTest;
import at.sbaresearch.mqtt4android.MqttTestHelper;
import at.sbaresearch.mqtt4android.PushTestHelper;
import at.sbaresearch.mqtt4android.registration.web.RegistrationResource;
import at.sbaresearch.mqtt4android.registration.web.RegistrationResource.DeviceRegisterDto;
import at.sbaresearch.mqtt4android.relay.web.PushResource;
import lombok.val;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;
import java.util.concurrent.TimeoutException;

import static at.sbaresearch.mqtt4android.PushTestHelper.*;
import static at.sbaresearch.mqtt4android.registration.RegistrationTestHelper.appReq;
import static at.sbaresearch.mqtt4android.registration.RegistrationTestHelper.deviceReq;
import static org.assertj.core.api.Assertions.*;

public class DeviceAndAppRegistrationTest extends AppTest {

  @Autowired
  RegistrationResource registrationResource;
  @Autowired
  PushResource pushResource;
  @Autowired
  MqttTestHelper mqttHelper;
  @Autowired
  PushTestHelper helper;

  // TODO multi-client tests
  //  routing of push messages to correct app

  @Test
  public void testDeviceAndAppRegistration_shouldReceiveMsg() throws Throwable {
    val reg = registrationResource.registerDevice(deviceReq().build());

    mqttHelper.withConnection(mqttHelper.setupClient(reg), connection -> {
      mqttHelper.subscribe(connection, reg.getMqttTopic());

      val mockUser = registrationToUser(reg);
      val senderId = "123456";
      val appResp = registrationResource.registerApp(
          appReq().senderId(senderId).build(), mockUser);

      val messageContent = "some push content";
      val msg = helper.pushMessageBuilder(messageContent)
          .token(appResp.getToken());
      pushResource.sendMessage(toReq(msg));

      val message = mqttHelper.await(connection.receive());
      // TODO better assert of payload
      assertThat(new String(message.getPayload())).contains(messageContent);
      assertThat(new String(message.getPayload())).contains(senderId);
    });
  }

  @Test
  public void testDeviceReg_wrongTopic_shouldFail() throws Throwable {
    val originalReg = registrationResource.registerDevice(deviceReq().build());

    val modifiedReg = originalReg.toBuilder().mqttTopic(originalReg.getMqttTopic() + "X")
        .build();
    mqttHelper.withConnection(mqttHelper.setupClient(modifiedReg), connection -> {
      // this does fail on the server, but client is not informed about this
      mqttHelper.subscribe(connection, modifiedReg.getMqttTopic());

      val mockUser = registrationToUser(originalReg);
      val appResp = registrationResource.registerApp(appReq().build(), mockUser);

      val msg = helper.pushMessageBuilder("should not be received")
          .token(appResp.getToken());
      pushResource.sendMessage(toReq(msg));

      // we wait for a timeout here
      assertThatThrownBy(() -> mqttHelper.await(connection.receive()))
          .isInstanceOf(TimeoutException.class);
    });
  }

  private User registrationToUser(DeviceRegisterDto reg) {
    return new User(reg.getMqttTopic(), "", Collections.emptyList());
  }

}
