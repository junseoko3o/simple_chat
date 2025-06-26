package com.simple.chat.service;

import com.simple.chat.config.jwt.JwtTokenProvider;
import com.simple.chat.dto.UserLoginRequestDto;
import com.simple.chat.dto.UserLoginResponseDto;
import com.simple.chat.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public UserLoginResponseDto login(UserLoginRequestDto userLoginRequestDto) {
        User user = userService.findOneUserByEmail(userLoginRequestDto.getEmail());
        if (!bCryptPasswordEncoder.matches(userLoginRequestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Password");
        };

        String accessToken = jwtTokenProvider.generateAccessToken(user);
        return new UserLoginResponseDto(
                user.getId(),
                user.getEmail(),
                user.getName(),
                accessToken
        );
    }

    public void logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationException("Invalid Access Token") {
            };
        }
//        User user = (User) authentication.getPrincipal();
        SecurityContextHolder.clearContext();
    }

    public UserLoginResponseDto getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new IllegalStateException("로그인되지 않았습니다.");
        }

        String email = authentication.getName();

        User user = userService.findOneUserByEmail(email);

        return new UserLoginResponseDto(
                user.getId(),
                user.getEmail(),
                user.getName(),
                null
        );
    }
}
