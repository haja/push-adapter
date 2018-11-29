package at.sbaresearch.mqtt4android.registration.web;

import at.sbaresearch.mqtt4android.registration.web.ParameterNamesAnnotationIntrospectorFix.ParameterExtractor;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.module.paramnames.PackageVersion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@Slf4j
public class WebConfig {

  @Bean
  public ObjectMapper jsonObjectMapper(ParameterNamesModuleFixed parameterNamesModuleFixed) {
    log.info("getting json object mapper");
    return new ObjectMapper()
        .registerModule(parameterNamesModuleFixed);
  }

  @Bean
  public ParameterNamesModuleFixed propertiesParamNamesModuleFixed() {
    return new ParameterNamesModuleFixed(Mode.PROPERTIES);
  }

  private static class ParameterNamesModuleFixed extends SimpleModule {
    private static final long serialVersionUID = 1L;

    private final JsonCreator.Mode creatorBinding;

    public ParameterNamesModuleFixed(JsonCreator.Mode creatorBinding) {
      super(PackageVersion.VERSION);
      this.creatorBinding = creatorBinding;
    }

    public ParameterNamesModuleFixed() {
      super(PackageVersion.VERSION);
      this.creatorBinding = null;
    }

    @Override
    public void setupModule(SetupContext context) {
      super.setupModule(context);
      context.insertAnnotationIntrospector(new ParameterNamesAnnotationIntrospectorFix(creatorBinding, new ParameterExtractor()) {

      });
    }

    @Override
    public int hashCode() { return getClass().hashCode(); }

    @Override
    public boolean equals(Object o) { return this == o; }
  }

}
