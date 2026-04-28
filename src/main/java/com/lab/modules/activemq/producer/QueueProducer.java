package com.lab.modules.activemq.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lab.modules.activemq.dto.OrderMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import static com.lab.modules.activemq.QueueList.*;

@Slf4j
@Service
public class QueueProducer {

    private final JmsTemplate queueJmsTemplate;

    public QueueProducer(@Qualifier("queueJmsTemplate") JmsTemplate queueJmsTemplate) {
        this.queueJmsTemplate = queueJmsTemplate;
    }

    public void sendOrder(OrderMessage order) {
        log.info("=== Sending order to queue [{}]: orderId={}", ORDER_QUEUE, order.getOrderId());
        queueJmsTemplate.convertAndSend(ORDER_QUEUE, order);
    }

    public void sendTextMessage(String text) {
        log.info("=== Sending text to queue [{}]", ORDER_TEXT_QUEUE);
        queueJmsTemplate.convertAndSend(ORDER_TEXT_QUEUE, text);
    }

    public void sendOrderWithPriority(OrderMessage order, String priority) {
        log.info("=== High-priority order sent: {} (priority={})", order.getOrderId(), priority);
        queueJmsTemplate.convertAndSend(PRIORITY_ORDER_QUEUE, order, message -> {
            message.setIntProperty("JMSXDeliveryCount", 0);
            message.setStringProperty("X-Priority", "HIGH"); // Change Priority here
            message.setStringProperty("X-Source", "REST-API");
            return message;
        });
    }

    public void sendTextRawQueue(String text) {
        log.info("=== Sending raw-text to queue [{}]", RAW_QUEUE);
        queueJmsTemplate.convertAndSend(RAW_QUEUE, text);
    }

    public void sendTestDlqQueue(OrderMessage order) throws JsonProcessingException {
        log.info("=== Sending order to queue [{}]: orderId={}", TEST_DLQ_QUEUE, order.getOrderId());
        queueJmsTemplate.convertAndSend(TEST_DLQ_QUEUE, order);
    }
}
