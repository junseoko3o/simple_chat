package com.simple.chat.controller;

import com.simple.chat.dto.UserLoginRequestDto;
import com.simple.chat.dto.UserLoginResponseDto;
import com.simple.chat.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDto> login(@Validated @RequestBody UserLoginRequestDto userLoginRequestDto) {
        UserLoginResponseDto userLoginResponseDto = authService.login(userLoginRequestDto);
        return ResponseEntity.ok(userLoginResponseDto);
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout() {
        authService.logout();
        return ResponseEntity.ok("로그아웃 완료!");
    }

    @GetMapping("/user/me")
    public ResponseEntity<UserLoginResponseDto> getCurrentUser() {
        return ResponseEntity.ok(authService.getCurrentUser());
    }
}
