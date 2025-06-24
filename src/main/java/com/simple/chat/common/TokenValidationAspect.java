package com.simple.chat.common;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
public class TokenValidationAspect {

    private final PermitAllUrls permitAllUrls;

    @Around("execution(* com.simple.chat..*Controller.*(..)) && !within(com.simple.chat.controller.SocketChatController)")
    public Object validateToken(ProceedingJoinPoint joinPoint) {
        HttpServletRequest request = getCurrentHttpRequest();
        String uri = request.getRequestURI();
        try {
            Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

            if (method.isAnnotationPresent(NoAuthRequired.class) || permitAllUrls.isPermitAll(uri)) {
                return joinPoint.proceed();
            }

            String token = extractToken(request);

            if (!isValidToken(token)) {
                throw new InvalidTokenException("유효하지 않은 토큰입니다.");
            }

            return joinPoint.proceed();

        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new InternalServerErrorException("서버 내부 오류가 발생했습니다.", e);
        }
    }

    private HttpServletRequest getCurrentHttpRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            throw new InternalServerErrorException("요청 정보를 가져올 수 없습니다.");
        }
        return attrs.getRequest();
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");

        if (header == null || header.isEmpty()) {
            throw new TokenNotFoundException("Authorization 헤더가 없습니다.");
        }

        if (!header.startsWith("Bearer ")) {
            throw new InvalidTokenFormatException("Authorization 헤더 형식이 올바르지 않습니다. (Bearer 토큰 필요)");
        }

        return header.substring(7);
    }

    private boolean isValidToken(String token) {
        return token != null && !token.isEmpty();
    }

    public static class InvalidTokenException extends RuntimeException {
        public InvalidTokenException(String message) {
            super(message);
        }
    }

    public static class TokenNotFoundException extends RuntimeException {
        public TokenNotFoundException(String message) {
            super(message);
        }
    }

    public static class InvalidTokenFormatException extends RuntimeException {
        public InvalidTokenFormatException(String message) {
            super(message);
        }
    }

    public static class InternalServerErrorException extends RuntimeException {
        public InternalServerErrorException(String message) {
            super(message);
        }
        public InternalServerErrorException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
