package at.sbaresearch.mqtt4android;

import at.sbaresearch.mqtt4android.registration.web.RegistrationResource;
import at.sbaresearch.mqtt4android.relay.web.PushResource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;

@Profile("!test-setup")
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  public void configure(HttpSecurity http) throws Exception {
    http
        .csrf().disable()
        .authorizeRequests()
        .antMatchers(RegistrationResource.REGISTRATION_DEVICE + "/**").permitAll()
        .antMatchers(PushResource.PUSH + "/**").permitAll()
        .anyRequest().authenticated();
    http.x509().subjectPrincipalRegex("CN=(.*?)(?:,|$)")
        .userDetailsService(userDetailsService());
  }
  // TODO add userDetailsService which grants roles based on authentication method (push token, client cert, no auth for device registration, ...)

  @Bean
  public UserDetailsService userDetailsService() {
    return username -> new User(username, "",
        AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_DEVICE"));
  }
}
