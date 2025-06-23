package com.simple.chat.controller;

import com.simple.chat.dto.MessageDto;
import com.simple.chat.service.SocketChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SocketChatController {

    private final SocketChatService socketChatService;

    @MessageMapping("/chat/message")
    @SendTo("/sub")
    public MessageDto chat(MessageDto messageDto) {
        return messageDto;
    }

    @MessageMapping("/chat/enter")
    @SendTo("/sub")
    public MessageDto newUser(MessageDto messageDto) {
        messageDto.setType(MessageDto.MessageType.ENTER);
        messageDto.setMessage(messageDto.getSender() + "입장");
        return messageDto;
    }
}
