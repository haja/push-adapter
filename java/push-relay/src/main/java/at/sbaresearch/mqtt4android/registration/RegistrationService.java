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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RegistrationService {

  private static int TOKEN_LENGTH = 32;

  RegistrationRepository repository;
  SecureRngGenerator rng;

  public String registerApp(AppRegistration registration) {
    // TODO verify app signature?

    val token = rng.randomString(TOKEN_LENGTH);
    repository.register(registration, token);

    return token;
  }

  public AppRegistration getApp(String token) {
    return repository.getTopic(token);
  }

  @Value
  public static class AppRegistration {
    String app;
    String signature;
    DeviceId deviceId;
    String senderId;
  }

  @Value
  public static class DeviceId {
    String id;
  }
}
