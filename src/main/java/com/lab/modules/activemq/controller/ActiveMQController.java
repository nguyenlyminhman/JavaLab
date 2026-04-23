package com.lab.modules.activemq.controller;

import com.lab.modules.activemq.dto.OrderMessage;
import com.lab.modules.activemq.producer.QueueProducer;
import com.lab.modules.activemq.producer.TopicProducer;
import jakarta.validation.Valid;
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
    public ResponseEntity<Map<String, Object>> sendOrderToQueue(
            @Valid @RequestBody OrderMessage order) {

        if (order.getOrderId() == null || order.getOrderId().isBlank()) {
            order = OrderMessage.createNew(
                    order.getCustomerId(), order.getProductName(),
                    order.getQuantity(), order.getTotalAmount());
        }

        queueProducer.sendOrder(order);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Order sent to queue",
                "orderId", order.getOrderId()
        ));
    }

    @PostMapping("/queue/text")
    public ResponseEntity<Map<String, String>> sendTextToQueue(
            @RequestParam String text) {
        queueProducer.sendTextMessage(text);
        return ResponseEntity.ok(Map.of("status", "sent", "text", text));
    }

    @PostMapping("/topic/broadcast")
    public ResponseEntity<Map<String, Object>> broadcastToTopic(
            @Valid @RequestBody OrderMessage order) {

        topicProducer.broadcastOrderUpdate(order);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Broadcast to topic subscribers",
                "orderId", order.getOrderId()
        ));
    }

    @GetMapping("/test")
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
