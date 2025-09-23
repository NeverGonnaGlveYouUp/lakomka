package com.lakomka.services.cart;

import com.lakomka.models.person.BasePerson;
import com.lakomka.models.product.PersonCartItem;
import com.lakomka.models.product.Product;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
public class CartService {

    @Autowired
    private GuestCartService guestCartService;

    @Autowired
    private UserCartService userCartService;

    public void addToCart(BasePerson user, Product cartItem,
                          HttpServletRequest request, Integer quantity) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (user == null || authentication instanceof AnonymousAuthenticationToken) {
            String sessionId = getCurrentSessionId(request)==null ? createSession(request) : getCurrentSessionId(request);
            guestCartService.addToCart(sessionId, cartItem, quantity);
        } else {
            userCartService.addToCart(user.getId(), cartItem, quantity);
        }
    }

    public Set<PersonCartItem> getCart(BasePerson user, HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (user == null || authentication instanceof AnonymousAuthenticationToken) {
            String sessionId = getCurrentSessionId(request)==null ? createSession(request) : getCurrentSessionId(request);
            return guestCartService.getCart(sessionId);
        } else {
            return userCartService.getCart(user.getId());
        }
    }

    private String generateSessionId() {
        return UUID.randomUUID().toString();
    }

    private String getCurrentSessionId(HttpServletRequest request) {
        if (request == null){
            return null;
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("sessionId".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private String createSession(HttpServletRequest request) {
        String sessionId = generateSessionId();
        Cookie newCookie = new Cookie("sessionId", sessionId);
        newCookie.setMaxAge(24 * 60 * 60); // Set expiry time (e.g., one day)
        request.setAttribute("sessionId", sessionId);
        return sessionId;
    }
}