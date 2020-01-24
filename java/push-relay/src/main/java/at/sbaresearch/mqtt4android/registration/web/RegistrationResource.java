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

package at.sbaresearch.mqtt4android.registration.web;

import at.sbaresearch.mqtt4android.registration.DeviceService;
import at.sbaresearch.mqtt4android.registration.DeviceService.DeviceData;
import at.sbaresearch.mqtt4android.registration.RegistrationService;
import at.sbaresearch.mqtt4android.registration.RegistrationService.AppRegistration;
import at.sbaresearch.mqtt4android.registration.RegistrationService.DeviceId;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
@AllArgsConstructor
public class RegistrationResource {

  public static final String REGISTRATION_DEVICE = "/registration/device";
  public static final String REGISTRATION_APP = "/registration/new";

  RegistrationService registrationService;
  DeviceService deviceService;

  // TODO exception handling

  // TODO are parameters needed at all?
  @PostMapping(path = REGISTRATION_DEVICE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public DeviceRegisterDto registerDevice(@RequestBody DeviceRegistrationRequest req) throws Exception {
    log.info("register new device");
    return toDeviceRegisterDto(deviceService.registerDevice());
  }

  private DeviceRegisterDto toDeviceRegisterDto(DeviceData data) {
    val conn = data.getMqttConnection();
    val keys = data.getClientKeys();
    return new DeviceRegisterDto(conn._1, conn._2,
        data.getMqttTopic(),
        keys.getEncodedPrivateKey(),
        keys.getEncodedCert());
  }

  @PostMapping(path = REGISTRATION_APP, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public AppRegistrationResponse registerApp(@RequestBody AppRegistrationRequest req,
      @AuthenticationPrincipal User user) {
    log.info("register app: {} for user: {}", req, user);

    val deviceId = toDeviceId(user);
    val registrationData = mapFromRequest(req, deviceId);
    val token = registrationService.registerApp(registrationData);

    return new AppRegistrationResponse(token);
  }

  private DeviceId toDeviceId(User user) {
    return new DeviceId(user.getUsername());
  }

  private AppRegistration mapFromRequest(AppRegistrationRequest req, DeviceId deviceId) {
    return new AppRegistration(req.app, req.signature, deviceId, req.senderId);
  }

  @Value
  @Builder(builderMethodName = "testWith")
  public static class AppRegistrationRequest {
    String app;
    String signature;
    String senderId;
  }

  @Value
  public static class AppRegistrationResponse {
    @NonNull
    String token;
  }

  @Value
  @Builder(builderMethodName = "testWith")
  public static class DeviceRegistrationRequest {
    String dummy;
  }

  @Builder(builderMethodName = "testWith", toBuilder = true)
  @Value
  public static class DeviceRegisterDto {
    @NonNull
    String host;
    int port;
    @NonNull
    String mqttTopic;
    @NonNull
    byte[] encodedPrivateKey;
    @NonNull
    byte[] encodedCert;
  }
}
