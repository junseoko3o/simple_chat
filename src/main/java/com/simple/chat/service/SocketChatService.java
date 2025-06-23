package com.simple.chat.service;

import com.simple.chat.dto.MessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SocketChatService {

    public MessageDto chat(MessageDto messageDto) {
        return messageDto;
    }

    public MessageDto enter(MessageDto messageDto) {
        return MessageDto.builder()
                .type(MessageDto.MessageType.ENTER)
                .sender(messageDto.getSender())
                .message(messageDto.getSender() + "입장")
                .build();
    }
}
