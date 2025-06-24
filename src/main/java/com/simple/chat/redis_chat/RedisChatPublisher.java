package com.simple.chat.redis_chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simple.chat.dto.MessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisChatPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public void saveMessage(String roomId, MessageDto message) {
        String key = "chat:messages:" + roomId;
        redisTemplate.opsForList().rightPush(key, message);
        redisTemplate.expire(key, 30, TimeUnit.MINUTES);
    }

    public void publishMessage(String roomId, MessageDto message) {
        String channel = "chatroom:" + roomId;
        redisTemplate.convertAndSend(channel, message);
    }

    public void updateChatRooms(String sender, String receiver) {
        redisTemplate.opsForSet().add("chat:rooms:" + sender, receiver);
        redisTemplate.opsForSet().add("chat:rooms:" + receiver, sender);
    }
}
