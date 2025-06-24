package com.simple.chat.common;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PermitAllUrls {

    private final List<String> permitAllPaths = List.of(
            "/chat.html",
            "/sign-up",
            "/login",
            "/ws-chat/**"
    );

    public List<String> getPermitAllPaths() {
        return permitAllPaths;
    }

    public boolean isPermitAll(String uri) {
        return permitAllPaths.contains(uri);
    }
}
