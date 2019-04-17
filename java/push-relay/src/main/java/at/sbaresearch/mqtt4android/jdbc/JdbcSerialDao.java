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
