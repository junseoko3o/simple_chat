package com.simple.chat.controller;

import com.simple.chat.dto.MessageDto;
import com.simple.chat.service.RedisChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
public class RedisChatController {

    private final RedisChatService redisChatService;

    @GetMapping("/chat/rooms")
    public ResponseEntity<Set<String>> getChatRooms(@RequestParam String email) {
        Set<String> chatPartners = redisChatService.getChatPartners(email);
        return ResponseEntity.ok(chatPartners);
    }

    @GetMapping("/chat/messages")
    public ResponseEntity<List<MessageDto>> getMessages(@RequestParam String roomId) {
        List<MessageDto> messages = redisChatService.getMessages(roomId);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/send")
    public ResponseEntity<Void> sendMessage(@RequestBody MessageDto messageDto) {
        redisChatService.sendMessage(messageDto);
        return ResponseEntity.ok().build();
    }
}
