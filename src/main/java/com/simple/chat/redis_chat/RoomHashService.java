package com.simple.chat.redis_chat;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
@Service
public class RoomHashService {

    public String getRoomId(String sender, String receiver) {
        if (sender == null || receiver == null) throw new IllegalArgumentException("sender, receiver cannot be null");

        String[] users = {sender.trim().toLowerCase(), receiver.trim().toLowerCase()};
        Arrays.sort(users);
        String joined = users[0] + ":" + users[1];

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(joined.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }
}
