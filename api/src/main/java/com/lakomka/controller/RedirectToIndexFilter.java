package com.lakomka.controller;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class RedirectToIndexFilter implements Filter {

    private static final List<String> TO_FILTER = List.of("/api", "/bundle", "/swagger-ui", "/v3/api-docs");

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        String requestURI = req.getRequestURI();

        if (TO_FILTER.stream().anyMatch(requestURI::startsWith)) {
            chain.doFilter(request, response);
        } else {
            request.getRequestDispatcher("/").forward(request, response);
        }

    }

}
