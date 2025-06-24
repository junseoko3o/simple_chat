package com.simple.chat.service;

import com.simple.chat.dto.MessageDto;
import com.simple.chat.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SocketChatService {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final UserService userService;

    public MessageDto chat(MessageDto messageDto) {
        User receiverUser = userService.findOneUserByEmail(messageDto.getReceiver());
        if (receiverUser == null) {
            throw new IllegalArgumentException("수신자를 찾을 수 없습니다.");
        }

        simpMessagingTemplate.convertAndSendToUser(
                messageDto.getReceiver(),
                "/queue/messages",
                messageDto
        );

        simpMessagingTemplate.convertAndSendToUser(
                messageDto.getSender(),
                "/queue/messages",
                messageDto
        );

        return messageDto;
    }

    public MessageDto enter(MessageDto messageDto) {
        return messageDto;
    }
}

