package com.simple.chat.config.jwt;

import com.simple.chat.entity.User;
import com.simple.chat.service.UserDetailService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ServerErrorException;

import java.io.IOException;

@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailService userDetailService;
    private final static String HEADER_AUTHORIZATION = "Authorization";
    private final static String TOKEN_PREFIX = "Bearer";
    private final static String NEW_ACCESS_TOKEN = "newAccessToken";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {
        String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);
        String token = getAccessToken(authorizationHeader);
        if (token != null) {
            if (jwtTokenProvider.validateToken(token)) {
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                handleExpiredAccessToken(request, response);
            }
        }
        filterChain.doFilter(request, response);
    }

    private String getAccessToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
            return authorizationHeader.substring(TOKEN_PREFIX.length()).trim();
        }
        return null;
    }

    private void setAuthentication(String token) {
        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void handleExpiredAccessToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            String token = getAccessToken(request.getHeader(HEADER_AUTHORIZATION));
            Claims claims = jwtTokenProvider.expiredTokenGetPayload(token);
            String email = claims.get("email", String.class);
            User user = userDetailService.loadUserByUsername(email);
            if (!jwtTokenProvider.validateRefreshToken(user)) {
                throw new AuthenticationException("Invalid RefreshToken") {
                };
            }

            String newAccessToken = jwtTokenProvider.generateAccessToken(user);
            setAuthentication(newAccessToken);
            response.setHeader(NEW_ACCESS_TOKEN, newAccessToken);
            request.setAttribute(NEW_ACCESS_TOKEN, newAccessToken);


        } catch (Exception e) {
            throw new ServerErrorException("Invalid RefreshToken", e);
        }
    }
}
