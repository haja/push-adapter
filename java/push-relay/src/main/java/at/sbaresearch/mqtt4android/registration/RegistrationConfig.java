package at.sbaresearch.mqtt4android.registration;

import at.sbaresearch.mqtt4android.common.SecureRngGenerator;
import at.sbaresearch.mqtt4android.registration.crypto.ClientKeyFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RegistrationConfig {

  @Bean
  public DeviceService deviceService(@Value("${mqtt.hostname}") String mqttHostname,
      @Value("${mqtt.port}") int mqttPort,
      ClientKeyFactory clientKeyFactory,
      SecureRngGenerator secureRngGenerator) {
    return new DeviceService(mqttHostname, mqttPort, clientKeyFactory, secureRngGenerator);
  }

}
