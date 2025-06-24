package com.simple.chat.service;

import com.simple.chat.dto.UserSignUpRequestDto;
import com.simple.chat.entity.User;
import com.simple.chat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> findAllUser() {
        return userRepository.findAll();
    }

    public User findOneUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Not found user."));
    }

    public User findOneUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Not found user."));
    }

    public User signUpUser(UserSignUpRequestDto userSignUpRequestDto) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        User user = User.builder()
                .email(userSignUpRequestDto.getEmail())
                .name(userSignUpRequestDto.getName())
                .password(encoder.encode(userSignUpRequestDto.getPassword()))
                .build();
        return userRepository.save(user);
    }
}
