package com.simple.chat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simple.chat.dto.MessageDto;
import com.simple.chat.redis_chat.RedisChatPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RedisChatService {

    private final RedisChatPublisher publisher;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public List<MessageDto> getMessages(String roomId) {
        List<Object> rawMessages = redisTemplate.opsForList().range("chat:messages:" + roomId, 0, -1);
        if (rawMessages == null) return List.of();

        return rawMessages.stream()
                .map(obj -> objectMapper.convertValue(obj, MessageDto.class))
                .collect(Collectors.toList());
    }

    public Set<String> getChatPartners(String email) {
        Set<Object> raw = redisTemplate.opsForSet().members("chat:rooms:" + email);
        if (raw == null) return Set.of();
        return raw.stream().map(Object::toString).collect(Collectors.toSet());
    }

    public void sendMessage(MessageDto message) {
        String roomId = getRoomId(message.getSender(), message.getReceiver());
        publisher.saveMessage(roomId, message);
        publisher.publishMessage(roomId, message);
        publisher.updateChatRooms(message.getSender(), message.getReceiver());
    }

    private String getRoomId(String a, String b) {
        return Arrays.asList(a, b).stream()
                .sorted()
                .collect(Collectors.joining(":"));
    }
}
