package com.simple.chat.controller;

import com.simple.chat.dto.UserResponseDto;
import com.simple.chat.dto.UserSignUpRequestDto;
import com.simple.chat.entity.User;
import com.simple.chat.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping()
    public List<UserResponseDto> findAllUser() {
        List<User> users = userService.findAllUser();
        return users.stream()
                .map(UserResponseDto::userResponseDto)
                .toList();
    }

    @PostMapping("/sign-up")
    public ResponseEntity<UserResponseDto> signUpUser(@Validated @RequestBody UserSignUpRequestDto userSignUpRequestDto) {
        User user = userService.signUpUser(userSignUpRequestDto);
        return ResponseEntity.ok(UserResponseDto.userResponseDto(user));
    }
}
