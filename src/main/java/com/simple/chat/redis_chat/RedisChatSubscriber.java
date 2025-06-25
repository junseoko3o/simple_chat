package com.simple.chat.redis_chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simple.chat.dto.MessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisChatSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messagingTemplate;

    /**
     * Redis에서 메시지 수신 시 호출됨
     * 메시지를 JSON -> MessageDto로 변환 후 WebSocket 메시지로 해당 사용자에게 전달
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String messageBody = new String(message.getBody());
            MessageDto messageDto = objectMapper.readValue(messageBody, MessageDto.class);

            messagingTemplate.convertAndSendToUser(
                    messageDto.getReceiver(),
                    "/sub/messages",
                    messageDto
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
