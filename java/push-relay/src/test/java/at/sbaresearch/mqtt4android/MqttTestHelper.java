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

import at.sbaresearch.mqtt4android.registration.crypto.CryptoTestHelper;
import at.sbaresearch.mqtt4android.registration.web.RegistrationResource.DeviceRegisterDto;
import io.vavr.CheckedConsumer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.fusesource.mqtt.client.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Profile("it")
@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class MqttTestHelper {

  CryptoTestHelper cryptoHelper;

  public MQTT setupClient(DeviceRegisterDto reg) throws Exception {
    val pinningFactory = cryptoHelper.createPinningFactory(reg.getEncodedPrivateKey(), reg.getEncodedCert());
    val client = new MQTT();
    client.setHost("ssl://" + reg.getHost() + ":" + reg.getPort());
    client.setSslContext(pinningFactory.getSslContext());
    return client;
  }

  public void withConnection(final MQTT client, CheckedConsumer<FutureConnection> withConnection)
      throws Throwable {
    val connection = client.futureConnection();
    connection.connect().await(2L, TimeUnit.SECONDS);
    try {
      withConnection.accept(connection);
    } finally {
      connection.disconnect();
    }
  }

  public void subscribe(FutureConnection connection, String topic)
      throws Exception {
    await(connection.subscribe(new Topic[]{
        new Topic(topic, QoS.AT_LEAST_ONCE)
    }));
  }

  public <T> T await(final Future<T> future) throws Exception {
    return future.await(500L, TimeUnit.MILLISECONDS);
  }
}
