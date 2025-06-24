package com.simple.chat.dto;

import lombok.Builder;
import lombok.Getter;
@Getter
@Builder
public class UserLoginResponseDto {
    private Long id;
    private String email;
    private String name;
    private String accessToken;

    public UserLoginResponseDto(Long id, String email, String name, String accessToken) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.accessToken = accessToken;
    }
}
