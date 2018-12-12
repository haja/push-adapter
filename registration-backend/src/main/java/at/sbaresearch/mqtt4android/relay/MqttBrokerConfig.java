package at.sbaresearch.mqtt4android.relay;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import javax.jms.ConnectionFactory;
import javax.jms.QueueConnectionFactory;

@Configuration
@EnableJms
public class MqttBrokerConfig {

  public static final String MQTT_MOCK_TOPIC = "foo";
  private final String embeddedBrokerName = "embeddedManual";

  @Bean
  public JmsListenerContainerFactory<?> queueListenerFactory(
      ConnectionFactory connectionFactory,
      DefaultJmsListenerContainerFactoryConfigurer configurer) {
    DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
    factory.setMessageConverter(messageConverter());
    configurer.configure(factory, connectionFactory);
    return factory;
  }

  @Bean
  public MessageConverter messageConverter() {
    MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
    converter.setTargetType(MessageType.TEXT);
    converter.setTypeIdPropertyName("_type");
    return converter;
  }

  @Bean
  public QueueConnectionFactory jmsConnectionFactory() {
    ActiveMQConnectionFactory connectionFactory =
        new ActiveMQConnectionFactory("vm://" + embeddedBrokerName);
    return connectionFactory;
  }

  @Bean
  public JmsTemplate jmsTopicTemplate() {
    JmsTemplate jmsTemplate = new JmsTemplate();
    jmsTemplate.setConnectionFactory(jmsConnectionFactory());
    jmsTemplate.setPubSubDomain(true);
    return jmsTemplate;
  }

  @Bean
  public BrokerService brokerService() throws Exception {
    BrokerService broker = new BrokerService();
    broker.setBrokerName(embeddedBrokerName);
    broker.setPersistent(false);
    // broker.setUseJmx(false);
    // TODO add plugins before connectors
    broker.addConnector("mqtt://localhost:61613");
    return broker;
  }

}
