package com.simple.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDto {
    private String sender;
    private String receiver;
    private String message;
    private String roomId;
    private MessageType type;

    public enum MessageType {
        ENTER, CHAT, LEAVE
    }
}
