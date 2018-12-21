package at.sbaresearch.mqtt4android.sample.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@SpringBootApplication
public class SampleAppBackendApplication {

  public static void main(String[] args) {
    SpringApplication.run(SampleAppBackendApplication.class, args);
  }

  /* is this needed?
  @Bean
  public FilterRegistrationBean requestDumperFilter() {
    val registration = new FilterRegistrationBean();
    val requestDumperFilter = new RequestDumperFilter();
    registration.setFilter(requestDumperFilter);
    registration.addUrlPatterns("/*");
    return registration;
  }
  */

  @Bean
  public CommonsRequestLoggingFilter logFilter() {
    CommonsRequestLoggingFilter filter
        = new CommonsRequestLoggingFilter();
    filter.setIncludeQueryString(true);
    filter.setIncludePayload(true);
    filter.setMaxPayloadLength(10000);
    filter.setIncludeHeaders(false);
    filter.setAfterMessagePrefix("REQUEST DATA : ");
    return filter;
  }
}
