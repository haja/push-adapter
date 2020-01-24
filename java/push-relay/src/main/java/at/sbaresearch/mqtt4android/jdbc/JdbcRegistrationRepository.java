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

package at.sbaresearch.mqtt4android.jdbc;

import at.sbaresearch.mqtt4android.registration.RegistrationRepository;
import at.sbaresearch.mqtt4android.registration.RegistrationService.AppRegistration;
import at.sbaresearch.mqtt4android.registration.RegistrationService.DeviceId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.springframework.stereotype.Repository;

@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Repository
public class JdbcRegistrationRepository implements RegistrationRepository {

  //language=SQL
  private static final String INSERT_TOKEN =
      "INSERT INTO app_registrations(token, device_id, app, app_signature, sender_id) VALUES (:token, :device_id, :app, :signature, :sender_id)";
  //language=SQL
  private static final String SELECT_TOPIC =
      "SELECT device_id, app, app_signature, sender_id FROM app_registrations WHERE token=:token";

  Jdbi jdbi;

  @Override
  public void register(AppRegistration registration, String token) {
    // TODO should re-registering drop old token for the same app and device?
    jdbi.useHandle(h -> h.createUpdate(INSERT_TOKEN)
        .bind("token", token)
        .bind("device_id", registration.getDeviceId().getId())
        .bind("app", registration.getApp())
        .bind("signature", registration.getSignature())
        .bind("sender_id", registration.getSenderId())
        .execute());
  }

  @Override
  public AppRegistration getTopic(String token) {
    RowMapper<AppRegistration> mapper = (rs, ctx) ->
        new AppRegistration(
            rs.getString("app"),
            rs.getString("app_signature"),
            new DeviceId(rs.getString("device_id")),
            rs.getString("sender_id")
        );
    return jdbi.withHandle(h -> h.select(SELECT_TOPIC)
        .bind("token", token)
        .map(mapper)
        .findOnly());
  }
}
