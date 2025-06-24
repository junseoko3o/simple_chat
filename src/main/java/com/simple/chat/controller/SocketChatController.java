package com.simple.chat.controller;

import com.simple.chat.dto.MessageDto;
import com.simple.chat.service.SocketChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class SocketChatController {

    private final SocketChatService socketChatService;

    @MessageMapping("/chat/message")
    @SendTo("/sub")
    public MessageDto chat(MessageDto messageDto) {
        return socketChatService.chat(messageDto);
    }

    @MessageMapping("/chat/enter")
    @SendTo("/sub")
    public MessageDto newUser(MessageDto messageDto) {
        return socketChatService.enter(messageDto);
    }

//    @MessageMapping("/chat/group/message")
//    public void groupChat(MessageDto messageDto) {
//        socketChatService.sendToGroup(messageDto.getRoomId(), messageDto);
//    }
}
