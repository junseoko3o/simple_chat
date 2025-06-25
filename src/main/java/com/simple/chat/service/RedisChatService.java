package com.simple.chat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simple.chat.dto.MessageDto;
import com.simple.chat.redis_chat.RedisChannelSubscriptionManager;
import com.simple.chat.redis_chat.RedisChatPublisher;
import com.simple.chat.redis_chat.RoomHashService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RedisChatService {

    private final RedisChatPublisher publisher;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final RedisChannelSubscriptionManager redisChannelSubscriptionManager;
    private final RoomHashService roomHashService;  // 주입

    private static final String MESSAGE_KEY_PREFIX = "chat:messages:";
    private static final String ROOM_KEY_PREFIX = "chat:rooms:";

    // roomId 대신 sender, receiver 받아서 내부에서 생성
    public List<MessageDto> getMessages(String sender, String receiver) {
        String roomId = roomHashService.getRoomId(sender, receiver);

        List<Object> rawMessages = redisTemplate.opsForList().range(MESSAGE_KEY_PREFIX + roomId, 0, -1);
        if (rawMessages == null || rawMessages.isEmpty()) return List.of();

        return rawMessages.stream()
                .map(obj -> objectMapper.convertValue(obj, MessageDto.class))
                .collect(Collectors.toList());
    }

    public Set<String> getChatPartners(String email) {
        Set<Object> raw = redisTemplate.opsForSet().members(ROOM_KEY_PREFIX + email);
        if (raw == null || raw.isEmpty()) return Set.of();

        return raw.stream()
                .map(Object::toString)
                .collect(Collectors.toSet());
    }

    public void sendMessage(MessageDto message) {
        String roomId = roomHashService.getRoomId(message.getSender(), message.getReceiver());

        MessageDto messages = MessageDto.builder()
                .sender(message.getSender())
                .receiver(message.getReceiver())
                .message(message.getMessage())
                .type(message.getType())
                .roomId(roomId)
                .build();

        publisher.saveMessage(roomId, messages);
        publisher.publishMessage(roomId, messages);
        publisher.updateChatRooms(messages.getSender(), messages.getReceiver());
    }

    public String joinRoom(String sender, String receiver) {
        redisTemplate.opsForSet().add(ROOM_KEY_PREFIX + sender, receiver);
        redisTemplate.opsForSet().add(ROOM_KEY_PREFIX + receiver, sender);
        String roomId = roomHashService.getRoomId(sender, receiver);
        redisChannelSubscriptionManager.subscribeToRoom(roomId);
        return roomId;
    }

    public void leaveRoom(String sender, String receiver) {
        String roomId = roomHashService.getRoomId(sender, receiver);

        redisChannelSubscriptionManager.unsubscribeFromRoom(roomId);

        redisTemplate.opsForSet().remove(ROOM_KEY_PREFIX + sender, receiver);
        redisTemplate.opsForSet().remove(ROOM_KEY_PREFIX + receiver, sender);
        redisTemplate.delete(MESSAGE_KEY_PREFIX + roomId);
    }
}
