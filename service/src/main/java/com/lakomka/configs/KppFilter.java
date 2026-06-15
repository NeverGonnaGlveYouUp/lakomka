package com.lakomka.configs;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class KppFilter extends OncePerRequestFilter {

    public static final String KPP_ATTRIBUTE = "X-KPP-ID";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws java.io.IOException, ServletException {
        String kpp = request.getHeader(KPP_ATTRIBUTE);
        if (kpp == null) {
            var cookies = request.getCookies();
            if (cookies != null) {
                for (var c : cookies) {
                    if ("kpp".equals(c.getName())) {
                        kpp = c.getValue();
                        break;
                    }
                }
            }
        }
        if (kpp != null) {
            request.setAttribute(KPP_ATTRIBUTE, kpp);
        }
        filterChain.doFilter(request, response);
    }
}
