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

import at.sbaresearch.mqtt4android.common.SecureRngGenerator;
import at.sbaresearch.mqtt4android.registration.crypto.ClientKeyFactory;
import at.sbaresearch.mqtt4android.registration.crypto.ClientKeyFactory.ClientKeys;
import at.sbaresearch.mqtt4android.relay.TopicRegistry;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class DeviceService {

  private static final int CLIENT_ID_LENGTH = 32;

  String mqttHostname;
  int mqttPort;
  ClientKeyFactory clientKeyFactory;
  SecureRngGenerator secureRngGenerator;

  public DeviceData registerDevice() throws Exception {
    val clientId = generateClientId();
    log.info("registering new device; clientId: {}", clientId);
    val clientKeys = clientKeyFactory.createSignedKey(clientId);

    val topic = clientId;

    val mqttSettings = getMqttSettings();
    DeviceData deviceData = new DeviceData(clientKeys, topic, mqttSettings);
    log.info("device data generated: {}", deviceData);
    return deviceData;
  }

  private String generateClientId() {
    return secureRngGenerator.randomString(CLIENT_ID_LENGTH);
  }

  private Tuple2<String, Integer> getMqttSettings() {
    return Tuple.of(mqttHostname, mqttPort);
  }


  @Value
  public static class DeviceData {
    ClientKeys clientKeys;
    String mqttTopic;
    Tuple2<String, Integer> mqttConnection;
  }

}
