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

package at.sbaresearch.mqtt4android.relay;

import at.sbaresearch.mqtt4android.registration.RegistrationService;
import at.sbaresearch.mqtt4android.registration.RegistrationService.AppRegistration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;
import java.util.UUID;

@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class PushService {

  RegistrationService registrationService;
  ObjectMapper objectMapper;
  JmsTemplate jmsTemplate;

  public String pushMessage(String token, PushMessage msg) {
    val app = registrationService.getApp(token);
    val messageId = UUID.randomUUID().toString();
    log.info("pushing messageId {}: msg: {}", messageId, msg);
    try {
      sendAsJson(app, messageId, msg.data);
      return messageId;
    } catch (JsonProcessingException e) {
      log.error("cannot send message, json parsing failed", e);
      throw new PushMessageException("cannot convert message for sending: " + msg);
    }
  }

  private void sendAsJson(AppRegistration app, String msgId,
      Map<String, String> data) throws JsonProcessingException {
    val jsonMsg = objectMapper.writeValueAsString(
        MqttMessage.of(app.getApp(), app.getSignature(), msgId, data,
            System.currentTimeMillis(), app.getSenderId()));
    val deviceId = app.getDeviceId().getId();
    log.info("pushing messageId {}: toDevice: {}", msgId, deviceId);
    jmsTemplate.convertAndSend(deviceId, jsonMsg);
  }

  @Value(staticConstructor = "of")
  public static class MqttMessage {
    String app;
    String signature;
    String messageId;
    Map<String, String> data;
    long sentTime;
    String senderId;
  }

  @Value(staticConstructor = "of")
  public static class PushMessage {
    Map<String, String> data;
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  private class PushMessageException extends RuntimeException {
    public PushMessageException(String msg) {
      super(msg);
    }
  }
}
