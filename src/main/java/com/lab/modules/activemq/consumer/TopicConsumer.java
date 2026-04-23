package com.lab.modules.activemq.consumer;

import com.lab.modules.activemq.dto.OrderMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TopicConsumer {

    @JmsListener(destination = "${app.jms.topic.notification}", containerFactory = "topicListenerFactory", subscription = "email-service" )
    public void handleEmailNotification(@Payload OrderMessage order) {
        log.info("[EMAIL-SERVICE] Sending email for order: {}", order.getOrderId());
        // Gửi email...
    }

    @JmsListener( destination = "${app.jms.topic.notification}", containerFactory = "topicListenerFactory", subscription = "sms-service")
    public void handleSmsNotification(@Payload OrderMessage order) {
        log.info("[SMS-SERVICE] Sending SMS for order: {}", order.getOrderId());
        // Gửi SMS...
    }

    @JmsListener( destination = "${app.jms.topic.notification}", containerFactory = "topicListenerFactory", subscription = "analytics-service")
    public void handleAnalytics(@Payload OrderMessage order) {
        log.info("[ANALYTICS] Recording order: {} - amount: {}",
                order.getOrderId(), order.getTotalAmount());
        // Ghi log analytics...
    }
}

