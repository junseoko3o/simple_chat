package com.simple.chat.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaCouponConsumer {

    @KafkaListener(topics = "coupon-events", groupId = "coupon-group")
    public void listen(String message) {
    }
}
