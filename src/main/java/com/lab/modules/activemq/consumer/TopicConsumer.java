package com.lab.modules.activemq.consumer;

import com.lab.modules.activemq.dto.OrderMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TopicConsumer {

    // ── Subscriber 1: Email notification ─────────────────
    @JmsListener(
            destination = "${app.jms.topic.notification}",
            containerFactory = "topicListenerFactory",  // dùng topicListenerFactory!
            subscription = "email-service"              // durable subscription name
    )
    public void handleEmailNotification(@Payload OrderMessage order) {
        log.info("[EMAIL-SERVICE] Sending email for order: {}", order.getOrderId());
        // Gửi email...
    }

    // ── Subscriber 2: SMS notification ────────────────────
    @JmsListener(
            destination = "${app.jms.topic.notification}",
            containerFactory = "topicListenerFactory",
            subscription = "sms-service"
    )
    public void handleSmsNotification(@Payload OrderMessage order) {
        log.info("[SMS-SERVICE] Sending SMS for order: {}", order.getOrderId());
        // Gửi SMS...
    }

    // ── Subscriber 3: Analytics/Audit ─────────────────────
    @JmsListener(
            destination = "${app.jms.topic.notification}",
            containerFactory = "topicListenerFactory",
            subscription = "analytics-service"
    )
    public void handleAnalytics(@Payload OrderMessage order) {
        log.info("[ANALYTICS] Recording order: {} - amount: {}",
                order.getOrderId(), order.getTotalAmount());
        // Ghi log analytics...
    }
}

