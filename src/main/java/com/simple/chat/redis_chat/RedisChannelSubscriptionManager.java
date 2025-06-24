package com.simple.chat.redis_chat;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class RedisChannelSubscriptionManager {

    private final RedisMessageListenerContainer listenerContainer;
    private final MessageListenerAdapter listenerAdapter;

    private final Map<String, ChannelTopic> topicMap = new ConcurrentHashMap<>();

    /**
     * 이미 구독 중이면 무시, 아니면 RedisMessageListenerContainer에 리스너 등록
     * 실제로는 메시지 발행-구독 구조에서 Redis가 자동으로 관리하기 때문에
     * 여기서 직접 구독 제어는 보통 불필요함.
     */
    public void subscribeToRoom(String roomId) {
        String channelName = "chatroom:" + roomId;

        if (topicMap.containsKey(channelName)) {
            System.out.println("[Already subscribed] " + channelName);
            return;
        }

        ChannelTopic topic = new ChannelTopic(channelName);
        listenerContainer.addMessageListener(listenerAdapter, topic);
        topicMap.put(channelName, topic);
        System.out.println("[Subscribed] " + channelName);
    }

    /**
     * 구독 해제 - 리스너 제거
     * 마찬가지로 Redis의 메시지 발행/구독은 클라이언트가 연결 상태를 기반으로 하므로
     * 서버 측에서 이렇게 직접 해제하는 경우는 거의 없음.
     */
    public void unsubscribeFromRoom(String roomId) {
        String channelName = "chatroom:" + roomId;

        ChannelTopic topic = topicMap.remove(channelName);
        if (topic != null) {
            listenerContainer.removeMessageListener(listenerAdapter, topic);
            System.out.println("[Unsubscribed] " + channelName);
        }
    }
}
