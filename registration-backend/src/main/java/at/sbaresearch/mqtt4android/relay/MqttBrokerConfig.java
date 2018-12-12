package at.sbaresearch.mqtt4android.relay;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import java.net.URI;

@Configuration
@EnableJms
public class MqttBrokerConfig {

  public static final String MQTT_MOCK_TOPIC = "foo";

  @Bean
  public JmsListenerContainerFactory<?> queueListenerFactory() {
    DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
    factory.setMessageConverter(messageConverter());
    return factory;
  }

  @Bean
  public MessageConverter messageConverter() {
    MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
    converter.setTargetType(MessageType.TEXT);
    converter.setTypeIdPropertyName("_type");
    return converter;
  }

  //@Bean
  //public ActiveMQConnectionFactoryCustomizer connectorCustomizer() {

  //}

  @Bean
  public BrokerService brokerService() throws Exception {
    BrokerService broker = new BrokerService();
    broker.setBrokerName("embeddedManual");
    broker.setPersistent(false);
    // TODO add plugins before connectors
    broker.addConnector("mqtt://localhost:61613");
    return broker;
  }

}
