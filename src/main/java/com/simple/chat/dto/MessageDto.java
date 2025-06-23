package com.simple.chat.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageDto {
    private String sender;
    private String message;
    private MessageType type;

    public enum MessageType {
        ENTER, CHAT, LEAVE
    }

    public MessageDto(String sender, String message, MessageType type) {
        this.sender = sender;
        this.message = message;
        this.type = type;
    }
}
