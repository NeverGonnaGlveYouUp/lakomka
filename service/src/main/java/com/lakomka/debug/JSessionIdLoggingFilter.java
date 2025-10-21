package com.lakomka.debug;

import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@Profile("debug")
public class JSessionIdLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jsessionId = extractJSessionId(request);
        if (jsessionId != null && !jsessionId.isEmpty()) {
            log.info("JSESSIONID: {}", jsessionId);
        }
        filterChain.doFilter(request, response); // Продолжение обработки запроса
    }

    private String extractJSessionId(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("JSESSIONID".equals(cookie.getName())) {
                    return cookie.getValue(); // Получаем значение JSESSIONID
                }
            }
        }
        return "";
    }
}
