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

import at.sbaresearch.mqtt4android.registration.crypto.SerialDao;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jdbi.v3.core.Jdbi;
import org.springframework.stereotype.Repository;

@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Repository
public class JdbcSerialDao implements SerialDao {

  //language=SQL
  private static final String GET_AND_INCREMENT =
      "INSERT INTO serial_store(generated_at) VALUES (NOW())";

  Jdbi jdbi;

  @Override
  public long getAndIncrement() {
    return jdbi.withHandle(h -> h.createUpdate(GET_AND_INCREMENT)
        .executeAndReturnGeneratedKeys()
        .mapTo(Long.class)
        .findOnly());
  }
}
