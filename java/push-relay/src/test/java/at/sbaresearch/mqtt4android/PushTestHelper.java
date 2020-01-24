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

import at.sbaresearch.mqtt4android.relay.web.PushRequest;
import at.sbaresearch.mqtt4android.relay.web.PushRequest.Message;
import io.vavr.collection.HashMap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;


@Profile("it")
@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PushTestHelper {

  public Message.MessageBuilder pushMessageBuilder() {
    return Message.builder()
        .token("token-not-set")
        .data(HashMap.of("data1", "value1").toJavaMap());
  }

  public Message.MessageBuilder pushMessageBuilder(String messageData) {
    val data = HashMap.of("message", messageData);
    return pushMessageBuilder()
        .data(data.toJavaMap());
  }

  public static PushRequest toReq(Message.MessageBuilder msgBuilder) {
    return PushRequest.builder()
        .message(msgBuilder.build())
        .build();
  }
}
