package com.lab.modules.activemq.producer;

import com.lab.modules.activemq.dto.OrderMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import static com.lab.modules.activemq.TopicList.*;

@Slf4j
@Service
public class TopicProducer {

    private final JmsTemplate topicJmsTemplate;

    @Value("${app.jms.topic.notification}")
    private String notificationTopic;

    public TopicProducer(@Qualifier("topicJmsTemplate") JmsTemplate topicJmsTemplate) {
        this.topicJmsTemplate = topicJmsTemplate;
    }

    // Broadcast notification tới tất cả subscriber
    public void broadcastOrderUpdate(OrderMessage order) {
        log.info("Broadcasting to topic [{}]: orderId={}", NOTIFICATION_TOPIC, order.getOrderId());
        topicJmsTemplate.convertAndSend(NOTIFICATION_TOPIC, order);
    }

    // Gửi tới topic tùy chỉnh
    public void sendToTopic(String topicName, Object payload) {
        topicJmsTemplate.convertAndSend(topicName, payload);
        log.info("Sent to topic [{}]", topicName);
    }
}
