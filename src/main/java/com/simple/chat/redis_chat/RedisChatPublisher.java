package com.simple.chat.redis_chat;

import com.simple.chat.dto.MessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisChatPublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 채팅 메시지를 Redis 리스트에 저장하고 30분간 만료시간 설정
     */
    public void saveMessage(String roomId, MessageDto message) {
        String key = "chat:messages:" + roomId;
        redisTemplate.opsForList().rightPush(key, message);
        redisTemplate.expire(key, 30, TimeUnit.MINUTES);
    }

    /**
     * Redis Pub/Sub 채널에 메시지 발행
     * 채팅방(roomId)에 해당하는 채널로 메시지를 보냄
     */
    public void publishMessage(String roomId, MessageDto message) {
        String channel = "chatroom:" + roomId;
        redisTemplate.convertAndSend(channel, message);
    }

    /**
     * 채팅방 참여자간 채팅방 목록 업데이트 (서로의 채팅방 세트에 상대 추가)
     */
    public void updateChatRooms(String sender, String receiver) {
        redisTemplate.opsForSet().add("chat:rooms:" + sender, receiver);
        redisTemplate.opsForSet().add("chat:rooms:" + receiver, sender);
    }
}
