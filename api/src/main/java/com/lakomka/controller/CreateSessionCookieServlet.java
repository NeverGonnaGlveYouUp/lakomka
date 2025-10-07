package com.lakomka.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.UUID;

@WebServlet("/")
public class CreateSessionCookieServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String sessionId = UUID.randomUUID().toString();

        Cookie sessionCookie = new Cookie("sessionID", sessionId);
        sessionCookie.setMaxAge(24 * 60 * 60);

        request.setAttribute("sessionId", sessionId);
        response.addCookie(sessionCookie);
    }
}
