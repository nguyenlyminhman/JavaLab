package com.lab.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;


// Kích hoạt @JmsListener
@EnableJms
@Configuration
public class ActiveMqConfig {

    @Value("${spring.activemq.broker-url}")
    private String brokerUrl;

    @Value("${spring.activemq.username}")
    private String username;

    @Value("${spring.activemq.password}")
    private String password;

    // ── Connection Factory ──────────────────────────────
    @Bean
    public ActiveMQConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
        factory.setUserName(username);
        factory.setPassword(password);

        // Cho phép deserialize class của bạn - BẮT BUỘC với ActiveMQ 5.15.9+
        factory.setTrustedPackages(java.util.List.of("com.lab"));
        return factory;
    }

    // ── Jackson Message Converter (JSON) ──────────────────────────────
    @Bean
    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);      // Gửi dạng TextMessage
        converter.setTypeIdPropertyName("_type");       // Header để biết class nào

        // Custom ObjectMapper để handle Java 8 Date/Time
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        converter.setObjectMapper(mapper);
        return converter;
    }

    // ── JmsTemplate cho Queue ──────────────────────────────
    @Bean(name = "queueJmsTemplate")
    public JmsTemplate queueJmsTemplate() {
        JmsTemplate template = new JmsTemplate(connectionFactory());
        template.setMessageConverter(jacksonJmsMessageConverter());
        template.setPubSubDomain(false);    // Queue
        template.setDeliveryPersistent(true);
        template.setExplicitQosEnabled(true);
        template.setTimeToLive(60000);      // Message sống 60 giây
        return template;
    }

    // ── JmsTemplate cho Topic ──────────────────────────────
    @Bean(name = "topicJmsTemplate")
    public JmsTemplate topicJmsTemplate() {
        JmsTemplate template = new JmsTemplate(connectionFactory());
        template.setMessageConverter(jacksonJmsMessageConverter());
        template.setPubSubDomain(true);     // Topic
        return template;
    }

    // ── ListenerContainerFactory cho Queue ──────────────────────────────
    @Bean(name = "queueListenerFactory")
    public DefaultJmsListenerContainerFactory queueListenerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setMessageConverter(jacksonJmsMessageConverter());
        factory.setPubSubDomain(false);          // Queue
        factory.setConcurrency("3-10");          // Min 3, Max 10 threads
        factory.setSessionTransacted(true);      // Transaction support
        factory.setErrorHandler(t ->             // Error handling
                System.err.println("JMS Error: " + t.getMessage())
        );
        return factory;
    }

    // ── ListenerContainerFactory cho Topic ──────────────────────────────
    @Bean(name = "topicListenerFactory")
    public DefaultJmsListenerContainerFactory topicListenerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setMessageConverter(jacksonJmsMessageConverter());
        factory.setPubSubDomain(true);           // Topic
        factory.setConcurrency("1-5");
        factory.setErrorHandler(t ->
                System.err.println("Topic Error: " + t.getMessage())
        );
        return factory;
    }

}
