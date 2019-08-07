package at.sbaresearch.mqtt4android.relay.jaas;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("!test-setup")
@Configuration
@Slf4j
public class JavaSecurityConfigOverride {

  public JavaSecurityConfigOverride(@Value("${custom.java.security.auth.login.config}") String cfg) {
    System.setProperty("java.security.auth.login.config", cfg);
    log.info("java.security.auth.login.config set to " + cfg);
  }
}
