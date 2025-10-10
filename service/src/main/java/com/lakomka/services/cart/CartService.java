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
            guestCartService.addToCart(getCurrentSessionId(request), cartItem, quantity);
        } else {
            userCartService.addToCart(user.getId(), cartItem, quantity);
        }
    }

    public Set<PersonCartItem> getCart(BasePerson user, HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (user == null || authentication instanceof AnonymousAuthenticationToken) {
            return guestCartService.getCart(getCurrentSessionId(request));
        } else {
            return userCartService.getCart(user.getId());
        }
    }

    private String getCurrentSessionId(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("JSESSIONID".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}