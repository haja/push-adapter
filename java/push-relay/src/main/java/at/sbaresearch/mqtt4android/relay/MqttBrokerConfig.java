package at.sbaresearch.mqtt4android.relay;

import at.sbaresearch.mqtt4android.relay.jaas.JaasCertOnlyOrSimpleAuthenticationPlugin;
import io.vavr.control.Option;
import lombok.val;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerPlugin;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.SslContext;
import org.apache.activemq.security.AuthenticationUser;
import org.apache.activemq.security.SimpleAuthenticationPlugin;
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
import javax.net.ssl.KeyManager;
import javax.net.ssl.TrustManager;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableJms
public class MqttBrokerConfig {

  public static final String MQTT_MOCK_TOPIC = "foo";
  private static final String TOPIC_WRITE_USERNAME = "system";
  private final String embeddedBrokerName = "embeddedManual";
  public static final String mqttPort = "61613";
  public static final String externalIp = "0.0.0.0";

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
        new ActiveMQConnectionFactory(TOPIC_WRITE_USERNAME, " 2rwq powrweopr uqwoeoa orareaoiureao e", "vm://" + embeddedBrokerName);
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
  public SslContext sslContext(KeyManager[] keyManager, TrustManager[] trustManager) {
    SslContext sslContext = new SslContext(keyManager, trustManager, new SecureRandom());
    sslContext.setProtocol("TLSv1.2");
    return sslContext;
  }

  @Bean
  public JaasCertOnlyOrSimpleAuthenticationPlugin jaasCertificateOnlyAuthPlugin() {
    // TODO this is currently used for all authentication; replace with system access only
    val simpleAuthPlugin = new SimpleAuthenticationPlugin();

    // TODO better system user access required; generate password in memory on startup and set here?
    simpleAuthPlugin.setUsers(List.of(
        new AuthenticationUser(TOPIC_WRITE_USERNAME, " 2rwq powrweopr uqwoeoa orareaoiureao e", TopicRegistry.TOPIC_WRITE_PRINCIPAL_GROUP)
    ));
    return new JaasCertOnlyOrSimpleAuthenticationPlugin(simpleAuthPlugin);
  }

  @Bean
  public BrokerService brokerService(
      JaasCertOnlyOrSimpleAuthenticationPlugin certAuthenticationPlugin,
      TopicRegistry topicRegistry,
      SslContext sslContext)
      throws Exception {
    BrokerService broker = new BrokerService();
    broker.setBrokerName(embeddedBrokerName);
    broker.setPersistent(false);
    // broker.setUseJmx(false);

    broker.setSslContext(sslContext);

    // plugins must be added before connectors
    addPlugin(broker, certAuthenticationPlugin);
    addPlugin(broker, topicRegistry.getAuthorizationPlugin());

    broker.addConnector("mqtt+ssl://" + externalIp + ":" + mqttPort
        + "?needClientAuth=true"
    );
    return broker;
  }

  private void addPlugin(BrokerService broker, BrokerPlugin brokerPlugin) {
    val existing = broker.getPlugins();
    val brokerPlugins = Option.of(existing)
        .map(Arrays::asList).map(ArrayList::new)
        .getOrElse(ArrayList::new);
    brokerPlugins.add(brokerPlugin);
    broker.setPlugins(brokerPlugins.toArray(BrokerPlugin[]::new));
  }
}
