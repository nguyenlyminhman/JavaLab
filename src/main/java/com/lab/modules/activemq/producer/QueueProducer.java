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

    // ── Gửi Object (auto-serialize sang JSON) ─────────────
    public void sendOrder(OrderMessage order) {
        log.info("Sending order to queue [{}]: orderId={}", orderQueue, order.getOrderId());
        queueJmsTemplate.convertAndSend(orderQueue, order);
        log.debug("Order sent successfully: {}", order);
    }

    // ── Gửi kèm custom headers ────────────────────────────
    public void sendOrderWithPriority(OrderMessage order, int priority) {
        queueJmsTemplate.convertAndSend(orderQueue, order, message -> {
            message.setIntProperty("JMSXDeliveryCount", 0);
            message.setStringProperty("X-Priority", String.valueOf(priority));
            message.setStringProperty("X-Source", "REST-API");
            return message;
        });
        log.info("High-priority order sent: {} (priority={})", order.getOrderId(), priority);
    }

    // ── Gửi String đơn giản ───────────────────────────────
    public void sendTextMessage(String text) {
        queueJmsTemplate.convertAndSend(orderQueue, text);
    }
}
