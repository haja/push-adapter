package at.sbaresearch.mqtt4android.relay;

import at.sbaresearch.mqtt4android.relay.jaas.JaasCertificateOnlyAuthPlugin;
import com.sun.tools.javac.util.ArrayUtils;
import lombok.val;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerPlugin;
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
import java.util.ArrayList;
import java.util.Arrays;

@Configuration
@EnableJms
public class MqttBrokerConfig {

  public static final String MQTT_MOCK_TOPIC = "foo";
  private final String embeddedBrokerName = "embeddedManual";
  public static final String mqttPort = "61613";

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
  public JaasCertificateOnlyAuthPlugin jaasCertificateOnlyAuthPlugin() {
    return new JaasCertificateOnlyAuthPlugin();
  }

  @Bean
  public BrokerService brokerService(JaasCertificateOnlyAuthPlugin certAuthPlugin) throws Exception {
    BrokerService broker = new BrokerService();
    broker.setBrokerName(embeddedBrokerName);
    broker.setPersistent(false);
    // broker.setUseJmx(false);

    // plugins must be added before connectors
    addPlugin(broker, certAuthPlugin);

    broker.addConnector("mqtt://localhost:" + mqttPort);
    return broker;
  }

  private void addPlugin(BrokerService broker, BrokerPlugin brokerPlugin) {
    val existing = broker.getPlugins();
    val added = new ArrayList<>(Arrays.asList(existing));
    added.add(brokerPlugin);
    broker.setPlugins(added.toArray(BrokerPlugin[]::new));
  }

}
