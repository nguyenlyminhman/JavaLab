package com.lab.config;

import org.apache.activemq.RedeliveryPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActiveMQRedeliveryConfig {

    @Bean
    public RedeliveryPolicy redeliveryPolicy() {
        RedeliveryPolicy policy = new RedeliveryPolicy();
        policy.setMaximumRedeliveries(3);          // Retry tối đa 3 lần
        policy.setInitialRedeliveryDelay(1000);    // Chờ 1s trước retry đầu
        policy.setBackOffMultiplier(2.0);          // Exponential backoff: 1s, 2s, 4s
        policy.setUseExponentialBackOff(true);     // Bật exponential backoff
        policy.setMaximumRedeliveryDelay(10000);   // Max delay 10s
        return policy;
    }
}

