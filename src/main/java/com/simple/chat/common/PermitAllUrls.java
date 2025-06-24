package com.simple.chat.common;

import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.List;

@Component
public class PermitAllUrls {
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Getter
    private final List<String> permitAllPaths = List.of(
            "/chat.html",
            "/sign-up",
            "/login",
            "/ws-chat/**",
            "/redis.html",
            "/chat/**",
            "/send"
    );

    public boolean isPermitAll(String uri) {
        return permitAllPaths.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, uri));
    }
}
