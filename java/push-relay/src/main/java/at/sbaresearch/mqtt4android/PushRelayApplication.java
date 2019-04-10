package at.sbaresearch.mqtt4android;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Profile;

@Profile("!test-setup")
@SpringBootApplication
public class PushRelayApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
      return application.sources(PushRelayApplication.class);
    }

    public static void main(String[] args) throws Exception {
      SpringApplication.run(PushRelayApplication.class, args);
    }

  }
