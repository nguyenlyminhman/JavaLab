package com.lab.modules.activemq.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.lab.modules.activemq.dto.OrderMessage;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.JmsHeaders;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import static com.lab.modules.activemq.QueueList.*;
import static com.lab.modules.activemq.RetryPolicyEnum.getDelay;


@Slf4j
@Component
public class QueueConsumer extends AbstractConsumer{

    @Autowired
    private MessageConverter messageConverter;

    public QueueConsumer(JmsTemplate queueJmsTemplate) {
        super(queueJmsTemplate);
    }

    @JmsListener( destination = ORDER_QUEUE, containerFactory = "queueListenerFactory" )
    public void receiveOrder(@Payload OrderMessage order) {
        log.info("=== Received order from queue: {} ===", ORDER_QUEUE );
        log.info("Order ID   : {}", order.getOrderId());
        log.info("Customer   : {}", order.getCustomerId());
        log.info("Product    : {}", order.getProductName());
        log.info("Quantity   : {}", order.getQuantity());
        log.info("Amount     : {}", order.getTotalAmount());
        log.info("Status     : {}", order.getStatus());

        // TODO: Business logic ở đây
        processOrder(order);
    }

    @JmsListener( destination = ORDER_TEXT_QUEUE, containerFactory = "queueListenerFactory" )
    public void receiveOrderText(@Payload String order) {
        log.info("=== Received order from queue: {} ===", ORDER_TEXT_QUEUE );
        log.info("Order details : {}", order);
        // TODO: Business logic ở đây
    }

    @JmsListener(destination = RAW_QUEUE, containerFactory = "queueListenerFactory")
    public void receiveRawMessage(Message message) throws JMSException {
        log.info("=== Received order from queue: {} ===", RAW_QUEUE );
        if (message instanceof TextMessage textMsg) {
            String text = textMsg.getText();
            log.info("Raw text message: {}", text);
        }
    }

    @JmsListener( destination = PRIORITY_ORDER_QUEUE, containerFactory = "queueListenerFactory", selector = "X-Priority = HIGH" )
    public void receiveHighPriorityOrder(
            @Payload OrderMessage order,
            @Header(JmsHeaders.MESSAGE_ID) String messageId,
            @Header(value = "X-Source", required = false) String source) {

        log.info("=== Received order from queue: {} ===", PRIORITY_ORDER_QUEUE );
        log.info("HIGH PRIORITY order: {} from {}", messageId, source);

        // TODO: Business logic ở đây
        processOrder(order);
    }

    @JmsListener( destination = TEST_DLQ_QUEUE, containerFactory = "queueListenerFactory" )
    public void receiveTestDlqQueue(Message message) throws Exception {
        OrderMessage order = new OrderMessage();

        log.info("=== Received order from queue: {} ===", TEST_DLQ_QUEUE );
        try {
            order = (OrderMessage) messageConverter.fromMessage(message);

            if (order.getProductName().contains("Lỗi")) throw new Exception("Sản phẩm lỗi");

        } catch (Exception e) {
            int retryCount = message.propertyExists("retryCount") ? message.getIntProperty("retryCount") : 0;

            long delay = getDelay(TEST_DLQ_QUEUE, retryCount);

            if (delay == -1) {
                log.error("DROP message queue={} payload={}", TEST_DLQ_QUEUE, order);
                // TODO: handle the failed message (save to DB or move to next queue to handle)
                return;
            }

            retryCount++;
            scheduleRetry(message, new Gson().toJson(order).toString() , TEST_DLQ_QUEUE, retryCount, delay);
        }

    }

    private void processOrder(OrderMessage order) {
        log.info("Processing order ID: {}, {} ", order.getOrderId(), "\n===================");
    }
}
