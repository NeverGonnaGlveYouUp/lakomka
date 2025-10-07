package com.lakomka.debug;

import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@Profile("debug")
public class JSessionIdLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String jsessionId = extractJSessionId(httpRequest);
        if (jsessionId != null && !jsessionId.isEmpty()) {
            log.info("JSESSIONID: {}", jsessionId);
        }
        chain.doFilter(request, response); // Продолжение обработки запроса
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
