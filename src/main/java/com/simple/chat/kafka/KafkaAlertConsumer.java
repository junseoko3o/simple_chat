package com.simple.chat.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaAlertConsumer {

    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(
            topics = "chat-alerts",
            groupId = "chat-alert-group"
    )
    public void listen(String message, @Header("kafka_receivedMessageKey") String toUser) {
        log.info("Received Kafka alert for {}: {}", toUser, message);

        messagingTemplate.convertAndSendToUser(
                toUser,
                "/sub/alerts",
                message
        );
        log.info("Sent WebSocket alert to {} on /sub/alerts", toUser);
    }
}