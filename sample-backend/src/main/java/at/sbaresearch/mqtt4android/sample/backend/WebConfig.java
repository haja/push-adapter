package at.sbaresearch.mqtt4android.sample.backend;

import at.sbaresearch.mqtt4android.jackson.fixes.single_parameter_ctor.ParameterNamesModuleFixed;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@Slf4j
public class WebConfig {

  /*
  @Bean
  public ObjectMapper jsonObjectMapper(ParameterNamesModuleFixed parameterNamesModuleFixed) {
    log.info("getting json object mapper");
    return new ObjectMapper()
        .registerModule(parameterNamesModuleFixed);
  }
  */

  @Bean
  public ObjectMapper jsonObjectMapper() {
    log.info("getting json object mapper");
    return new ObjectMapper()
        .registerModule(new ParameterNamesModule());
  }

  /*
  @Bean
  public ParameterNamesModuleFixed propertiesParamNamesModuleFixed() {
    return new ParameterNamesModuleFixed(Mode.PROPERTIES);
  }
  */

}
