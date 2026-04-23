package com.lab.modules.activemq.producer;

import com.lab.modules.activemq.dto.OrderMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class QueueProducer {

    private final JmsTemplate queueJmsTemplate;

    @Value("${app.jms.queue.order}")
    private String orderQueue;

    public QueueProducer(@Qualifier("queueJmsTemplate") JmsTemplate queueJmsTemplate) {
        this.queueJmsTemplate = queueJmsTemplate;
    }

    public void sendOrder(OrderMessage order) {
        log.info("Sending order to queue [{}]: orderId={}", orderQueue, order.getOrderId());
        queueJmsTemplate.convertAndSend(orderQueue, order);
        log.debug("Order sent successfully: {}", order);
    }

    public void sendOrderWithPriority(OrderMessage order, int priority) {
        queueJmsTemplate.convertAndSend(orderQueue, order, message -> {
            message.setIntProperty("JMSXDeliveryCount", 0);
            message.setStringProperty("X-Priority", String.valueOf(priority));
            message.setStringProperty("X-Source", "REST-API");
            return message;
        });
        log.info("High-priority order sent: {} (priority={})", order.getOrderId(), priority);
    }

    public void sendTextMessage(String text) {
        queueJmsTemplate.convertAndSend(orderQueue, text);
    }
}
