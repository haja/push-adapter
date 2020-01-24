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

package at.sbaresearch.mqtt4android.relay.web;

import at.sbaresearch.mqtt4android.relay.PushService;
import at.sbaresearch.mqtt4android.relay.PushService.PushMessage;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(PushResource.PUSH)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
@AllArgsConstructor
public class PushResource {

  public static final String PUSH = "/push";

  PushService pushService;

  @PostMapping("/")
  public PushResponse sendMessage(@RequestBody PushRequest req) {
    val msg = req.getMessage();
    log.info("push message {} received", msg);
    val token = msg.getToken();

    return toResponse(pushService.pushMessage(token, toMessage(msg)));
  }

  private PushResponse toResponse(String pushMessage) {
    return new PushResponse("projects/custom_relay/messages/" + pushMessage);
  }

  private PushMessage toMessage(PushRequest.Message msg) {
    return PushMessage.of(msg.getData());
  }

}
