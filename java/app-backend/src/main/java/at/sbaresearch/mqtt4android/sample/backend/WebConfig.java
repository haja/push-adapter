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
