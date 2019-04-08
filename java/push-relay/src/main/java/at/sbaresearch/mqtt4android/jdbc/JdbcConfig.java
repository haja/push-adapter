package at.sbaresearch.mqtt4android.jdbc;

import org.jdbi.v3.core.Jdbi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class JdbcConfig {

  @Bean
  public Jdbi jdbi(DataSource ds) {
    return Jdbi.create(ds)
        .installPlugins();
  }

}
