package com.lab.modules.activemq.consumer;

import com.lab.modules.activemq.dto.OrderMessage;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.support.JmsHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class QueueConsumer {

    @JmsListener( destination = "order.queue", containerFactory = "queueListenerFactory" )
    public void receiveOrderText(@Payload String order) {
        log.info("=== Received order from queue ===");
        log.info("Order ID   : {}", order);

        // TODO: Business logic ở đây
    }

    @JmsListener( destination = "${app.jms.queue.order}", containerFactory = "queueListenerFactory" )
    public void receiveOrder(@Payload OrderMessage order) {
        log.info("=== Received order from queue ===");
        log.info("Order ID   : {}", order.getOrderId());
        log.info("Customer   : {}", order.getCustomerId());
        log.info("Product    : {}", order.getProductName());
        log.info("Quantity   : {}", order.getQuantity());
        log.info("Amount     : {}", order.getTotalAmount());
        log.info("Status     : {}", order.getStatus());

        // TODO: Business logic ở đây
        processOrder(order);
    }

    @JmsListener( destination = "priority.order.queue", containerFactory = "queueListenerFactory", selector = "X-Priority = 'HIGH'" )
    public void receiveHighPriorityOrder(
            @Payload OrderMessage order,
            @Header(JmsHeaders.MESSAGE_ID) String messageId,
            @Header(value = "X-Source", required = false) String source) {

        log.info("HIGH PRIORITY order: {} from {}", messageId, source);
        processOrder(order);
    }

    @JmsListener(destination = "raw.queue", containerFactory = "queueListenerFactory")
    public void receiveRawMessage(Message message) throws JMSException {
        if (message instanceof TextMessage textMsg) {
            String text = textMsg.getText();
            log.info("Raw text message: {}", text);
        }
    }

    private void processOrder(com.lab.modules.activemq.dto.OrderMessage order) {
        log.info("Processing order: {}", order.getOrderId());
    }

    @JmsListener(destination = "ActiveMQ.DLQ", containerFactory = "queueListenerFactory" )
    public void receiveDLQ(Message order) {
        log.info("Order ID asas  : {}", order);
        // TODO: Business logic ở đây

    }
}
