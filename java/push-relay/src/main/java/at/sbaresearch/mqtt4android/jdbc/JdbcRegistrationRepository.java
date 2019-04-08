package at.sbaresearch.mqtt4android.jdbc;

import at.sbaresearch.mqtt4android.registration.RegistrationRepository;
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
      "INSERT INTO app_registrations(token, topic) VALUES (:token, :topic)";
  //language=SQL
  private static final String SELECT_TOPIC =
      "SELECT topic FROM app_registrations WHERE token=:token";

  Jdbi jdbi;

  @Override
  public void register(String token, String topic) {
    jdbi.useHandle(h -> {
      h.createUpdate(INSERT_TOKEN)
          .bind("token", token)
          .bind("topic", topic)
          .execute();
    });
  }

  @Override
  public String getTopic(String token) {
    RowMapper<String> mapper = (rs, ctx) -> rs.getString("topic");
    return jdbi.withHandle(h -> h.select(SELECT_TOPIC)
        .bind("token", token)
        .map(mapper)
        .findOnly());
  }
}
