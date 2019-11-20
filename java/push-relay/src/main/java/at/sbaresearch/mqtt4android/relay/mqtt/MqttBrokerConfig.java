package at.sbaresearch.mqtt4android.relay.mqtt;

import at.sbaresearch.mqtt4android.common.SecureRngGenerator;
import at.sbaresearch.mqtt4android.relay.TopicRegistry;
import at.sbaresearch.mqtt4android.relay.jaas.JaasCertOnlyOrSimpleAuthenticationPlugin;
import io.vavr.control.Option;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerPlugin;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.SslContext;
import org.apache.activemq.security.AuthenticationUser;
import org.apache.activemq.security.SimpleAuthenticationPlugin;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
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
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Profile("!test-setup")
@Configuration
@EnableJms
@Slf4j
public class MqttBrokerConfig {

  public static final String TOPIC_WRITE_USERNAME = "system";
  public static final String externalIp = "0.0.0.0";
  private static final int SYSTEM_USER_PW_LENGTH = 48;

  private final String writeUserPassword;
  private final String embeddedBrokerName = "embeddedManual";

  public MqttBrokerConfig(SecureRngGenerator generator) {
    log.info("generating system user password...");
    writeUserPassword = generator.randomString(SYSTEM_USER_PW_LENGTH);
    log.info("system user password generated: {}", writeUserPassword);
  }

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
    return new ActiveMQConnectionFactory(TOPIC_WRITE_USERNAME,
        writeUserPassword, "vm://" + embeddedBrokerName);
  }

  @Bean
  public JmsTemplate jmsTopicTemplate() {
    JmsTemplate jmsTemplate = new JmsTemplate();
    jmsTemplate.setConnectionFactory(jmsConnectionFactory());
    jmsTemplate.setPubSubDomain(true);
    return jmsTemplate;
  }

  @Bean
  public SslContext sslContext(KeyManager[] keyManager, TrustManager[] trustManager,
      SecureRngGenerator secureRngGenerator) {
    SslContext sslContext =
        new SslContext(keyManager, trustManager, secureRngGenerator.getSecureRandom());
    sslContext.setProtocol("TLSv1.2");
    return sslContext;
  }

  @Bean
  public JaasCertOnlyOrSimpleAuthenticationPlugin jaasCertificateOnlyAuthPlugin() {
    val simpleAuthPlugin = new SimpleAuthenticationPlugin();
    simpleAuthPlugin.setUsers(List.of(
        new AuthenticationUser(TOPIC_WRITE_USERNAME, writeUserPassword, TopicRegistry.TOPIC_PRINCIPAL_WRITER)
    ));
    return new JaasCertOnlyOrSimpleAuthenticationPlugin(simpleAuthPlugin);
  }

  @Bean
  public BrokerService brokerService(
      @Value("${mqtt.port}") int mqttPort,
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
    ).setAllowLinkStealing(true);
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
