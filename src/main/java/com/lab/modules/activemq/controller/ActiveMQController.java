package com.lab.modules.activemq.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lab.modules.activemq.dto.OrderMessage;
import com.lab.modules.activemq.producer.QueueProducer;
import com.lab.modules.activemq.producer.TopicProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

        import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class ActiveMQController {

    private final QueueProducer queueProducer;
    private final TopicProducer topicProducer;

    @PostMapping("/queue/order")
    public ResponseEntity<Map<String, Object>> sendOrderToQueue() {

        OrderMessage order = OrderMessage.createNew(
                "CUST-001",
                "MacBook Pro M3",
                1,
                new BigDecimal("65000000")
        );

        queueProducer.sendOrder(order);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Order sent to queue",
                "orderId", order.getOrderId()
        ));
    }

    @PostMapping("/queue/order/text")
    public ResponseEntity<Map<String, String>> sendTextToQueue() {
        String text = "Message send/received by ORDER_TEXT_QUEUE and processed successfully without any errors.";
        queueProducer.sendTextMessage(text);
        return ResponseEntity.ok(Map.of("status", "sent", "text", text));
    }

    @PostMapping("/queue/raw/text")
    public ResponseEntity<Map<String, String>> sendTextToRawQueue() {
        String text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris.";
        queueProducer.sendTextRawQueue(text);
        return ResponseEntity.ok(Map.of("status", "sent", "text", text));
    }

    @PostMapping("/queue/order/priority")
    public ResponseEntity<Map<String, Object>> sendToPriorityQueue() {

        OrderMessage mockOrder = OrderMessage.createNew(
                "CUST-001",
                "MacBook Pro M3",
                1,
                new BigDecimal("65000000")
        );

        queueProducer.sendOrderWithPriority(mockOrder, "HIGH");

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Order with HIGH priority has been sent to queue",
                "orderId", mockOrder.getOrderId()
        ));
    }

    @PostMapping("/queue/order/retry")
    public ResponseEntity<Map<String, Object>> sendToRetryQueue() throws JsonProcessingException {

        OrderMessage mockOrder = OrderMessage.createNew(
                "CUST-001",
                "MacBook Pro M3 Lỗi",
                1,
                new BigDecimal("65000000")
        );

        queueProducer.sendTestDlqQueue(mockOrder);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Order Lỗi",
                "orderId", mockOrder.getOrderId()
        ));
    }

    @PostMapping("/topic/notification")
    public ResponseEntity<Map<String, Object>> broadcastToTopic() {

        OrderMessage mockOrder = OrderMessage.createNew(
                "CUST-001",
                "MacBook Pro M3",
                1,
                new BigDecimal("65000000")
        );

        topicProducer.broadcastOrderUpdate(mockOrder);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Broadcast to topic subscribers",
                "orderId", mockOrder.getOrderId()
        ));
    }

    @GetMapping("/both-queue-topic/test")
    public ResponseEntity<Map<String, Object>> sendTestMessage() {
        OrderMessage testOrder = OrderMessage.createNew(
                "CUST-001",
                "MacBook Pro M3",
                1,
                new BigDecimal("65000000")
        );

        queueProducer.sendOrder(testOrder);
        topicProducer.broadcastOrderUpdate(testOrder);

        return ResponseEntity.ok(Map.of(
                "status", "Test messages sent to both Queue and Topic",
                "orderId", testOrder.getOrderId()
        ));
    }
}
