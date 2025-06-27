package com.simple.chat.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {
    @Bean
    public NewTopic chatAlertsTopic() {
        return new NewTopic("chat-alerts", 1, (short) 1);
    }

    @Bean
    public NewTopic couponEventTopic() {
        return new NewTopic("coupon-events", 1, (short) 1);
    }
}
