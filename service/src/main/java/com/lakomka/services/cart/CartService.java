package com.lakomka.services.cart;

import com.lakomka.models.person.BasePerson;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class CartService {

    @Autowired
    private GuestCartService guestCartService;

    @Autowired
    private UserCartService userCartService;

    public ResponseEntity<?> addToCart(
            BasePerson user,
            Long productId,
            HttpServletRequest request,
            Integer quantity
    ) {
        if (user == null) {
            return guestCartService.addToCart(getCurrentSessionId(request), productId, quantity);
        } else {
            return userCartService.addToCart(user, productId, quantity);
        }
    }

    public HashMap<Long, Integer> getCartIdQuantityHashMap(
            BasePerson user,
            HttpServletRequest request
    ) {
        if (user == null) {
            return guestCartService.getCartIdQuantityHashMap(getCurrentSessionId(request));
        } else {
            return userCartService.getCartIdQuantityHashMap(user);
        }
    }


    public ResponseEntity<?> getCart(
            BasePerson user,
            HttpServletRequest request
    ) {
        if (user == null) {
            return guestCartService.getCart(getCurrentSessionId(request));
        } else {
            return userCartService.getCart(user);
        }
    }

    public ResponseEntity<?> getCartSummary(
            BasePerson user,
            HttpServletRequest request
    ) {
        if (user == null) {
            return guestCartService.getCartSummary(getCurrentSessionId(request));
        } else {
            return userCartService.getCartSummary(user);
        }
    }

    public void moveGuestCartToUserCart(BasePerson user, HttpServletRequest request) {
        String sessionId = getCurrentSessionId(request);
        if (sessionId != null) {
            // Получаем содержимое анонимной корзины
            HashMap<Long, Integer> guestCart = guestCartService.getCartIdQuantityHashMap(sessionId);

            // Переносим товары в пользовательскую корзину
            for (HashMap.Entry<Long, Integer> entry : guestCart.entrySet()) {
                userCartService.addToCart(user, entry.getKey(), entry.getValue());
            }

            // Очищаем анонимную корзину
            guestCartService.clearCart(sessionId);
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