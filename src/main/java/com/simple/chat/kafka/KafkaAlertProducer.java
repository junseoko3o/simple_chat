package com.simple.chat.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaAlertProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendEnterAlert(String receiverEmail, String senderName) {
        String msg = "[알림] " + senderName + "님이 입장했습니다.";
        log.info("Sending ENTER alert to {}: {}", receiverEmail, msg);
        kafkaTemplate.send("chat-alerts", receiverEmail, msg);
    }

    public void sendLeaveAlert(String receiverEmail, String senderName) {
        String msg = "[알림] " + senderName + "님이 퇴장했습니다.";
        log.info("Sending LEAVE alert to {}: {}", receiverEmail, msg);
        kafkaTemplate.send("chat-alerts", receiverEmail, msg);
    }
}
