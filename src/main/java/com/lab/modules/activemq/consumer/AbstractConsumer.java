package com.lab.modules.activemq.consumer;

import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.Enumeration;

@Service
abstract class AbstractConsumer {

    private final JmsTemplate queueJmsTemplate;

    public AbstractConsumer(@Qualifier("queueJmsTemplate") JmsTemplate queueJmsTemplate) {
        this.queueJmsTemplate = queueJmsTemplate;
    }

    void handleScheduleRetry(Message originalMessage, String payload, String queueName, int retryCount, long delay) throws Exception {

    }

    void scheduleRetry(Message originalMessage, String payload, String queueName, int retryCount, long delay) throws Exception {
        Thread.sleep(delay);
        queueJmsTemplate.send(queueName, session -> {
            TextMessage newMsg = session.createTextMessage(payload);
            // copy headers
            Enumeration<?> names = originalMessage.getPropertyNames();
            while (names.hasMoreElements()) {
                String key = (String) names.nextElement();
                newMsg.setObjectProperty(key, originalMessage.getObjectProperty(key));
            }

            newMsg.setIntProperty("retryCount", retryCount);
            newMsg.setLongProperty("AMQ_SCHEDULED_DELAY", delay);
            return newMsg;
        });
    }
}
