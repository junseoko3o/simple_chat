package com.simple.chat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simple.chat.dto.MessageDto;
import com.simple.chat.redis_chat.RedisChannelSubscriptionManager;
import com.simple.chat.redis_chat.RedisChatPublisher;
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

    private static final String MESSAGE_KEY_PREFIX = "chat:messages:";
    private static final String ROOM_KEY_PREFIX = "chat:rooms:";

    // 채팅 메시지 불러오기
    public List<MessageDto> getMessages(String roomId) {
        List<Object> rawMessages = redisTemplate.opsForList().range(MESSAGE_KEY_PREFIX + roomId, 0, -1);
        if (rawMessages == null || rawMessages.isEmpty()) return List.of();

        return rawMessages.stream()
                .map(obj -> objectMapper.convertValue(obj, MessageDto.class))
                .collect(Collectors.toList());
    }

    // 채팅 상대 목록 불러오기
    public Set<String> getChatPartners(String email) {
        Set<Object> raw = redisTemplate.opsForSet().members(ROOM_KEY_PREFIX + email);
        if (raw == null || raw.isEmpty()) return Set.of();

        return raw.stream()
                .map(Object::toString)
                .collect(Collectors.toSet());
    }

    // 메시지 보내기 처리
    public void sendMessage(MessageDto message) {
        String roomId = getRoomId(message.getSender(), message.getReceiver());
        publisher.saveMessage(roomId, message);
        publisher.publishMessage(roomId, message);
        publisher.updateChatRooms(message.getSender(), message.getReceiver());
    }

    // 채팅방 참여 (채팅 상대 목록에 추가)
    public void joinRoom(String sender, String receiver) {
        redisTemplate.opsForSet().add(ROOM_KEY_PREFIX + sender, receiver);
        redisTemplate.opsForSet().add(ROOM_KEY_PREFIX + receiver, sender);
        String roomId = getRoomId(sender, receiver);
        if (!redisTemplate.hasKey(MESSAGE_KEY_PREFIX + roomId)) {
            redisChannelSubscriptionManager.subscribeToRoom(roomId);
        }
    }

    // 채팅방 퇴장
    public void leaveRoom(String sender, String receiver) {
        String roomId = getRoomId(sender, receiver);

        // 구독 해제는 실제로 필요하지 않을 수도 있음. 필요시 주석 해제
         redisChannelSubscriptionManager.unsubscribeFromRoom(roomId);

        redisTemplate.opsForSet().remove(ROOM_KEY_PREFIX + sender, receiver);
        redisTemplate.opsForSet().remove(ROOM_KEY_PREFIX + receiver, sender);
        redisTemplate.delete(MESSAGE_KEY_PREFIX + roomId);

        System.out.printf("Room %s left. Redis data cleared.%n", roomId);
    }

    // roomId 생성 헬퍼
    public String getRoomId(String sender, String receiver) {
        if (sender == null || receiver == null) throw new IllegalArgumentException("sender, receiver cannot be null");
        return sender.compareTo(receiver) < 0 ? sender + ":" + receiver : receiver + ":" + sender;
    }
}
