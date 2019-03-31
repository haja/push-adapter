package at.sbaresearch.mqtt4android.registration;

import at.sbaresearch.mqtt4android.registration.crypto.ClientKeyFactory;
import at.sbaresearch.mqtt4android.relay.TopicRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RegistrationConfig {

  @Bean
  public DeviceService deviceService(@Value("${mqtt.hostname}") String mqttHostname,
      ClientKeyFactory clientKeyFactory,
      TopicRegistry topicRegistry) {
    return new DeviceService(mqttHostname, clientKeyFactory, topicRegistry);
  }

}
