package com.simple.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDto {
    private String sender;
    private String message;
    private MessageType type;

    public enum MessageType {
        ENTER, CHAT, LEAVE
    }
}
