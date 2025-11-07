package com.lakomka.services.cart;

import com.lakomka.models.person.BasePerson;
import com.lakomka.utils.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class CartService {

    private final GuestCartService guestCartService;
    private final UserCartService userCartService;
    private final SessionUtil sessionUtil;

    public ResponseEntity<?> addToCart(
            BasePerson user,
            Long productId,
            HttpServletRequest request,
            Integer quantity
    ) {
        if (user == null) {
            return guestCartService.addToCart(sessionUtil.getCurrentSessionId(request), productId, quantity);
        } else {
            return userCartService.addToCart(user, productId, quantity);
        }
    }

    public HashMap<Long, Integer> getCartIdQuantityHashMap(
            BasePerson user,
            HttpServletRequest request
    ) {
        if (user == null) {
            return guestCartService.getCartIdQuantityHashMap(sessionUtil.getCurrentSessionId(request));
        } else {
            return userCartService.getCartIdQuantityHashMap(user);
        }
    }


    public ResponseEntity<?> getCart(
            BasePerson user,
            HttpServletRequest request
    ) {
        if (user == null) {
            return guestCartService.getCart(sessionUtil.getCurrentSessionId(request));
        } else {
            return userCartService.getCart(user);
        }
    }

    public ResponseEntity<?> getCartSummary(
            BasePerson user,
            HttpServletRequest request
    ) {
        if (user == null) {
            return guestCartService.getCartSummary(sessionUtil.getCurrentSessionId(request));
        } else {
            return userCartService.getCartSummary(user);
        }
    }

    public void moveGuestCartToUserCart(BasePerson user, HttpServletRequest request) {
        String sessionId = sessionUtil.getCurrentSessionId(request);
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

    public ResponseEntity<?> clearCart(BasePerson user, HttpServletRequest request) {
        String sessionId = sessionUtil.getCurrentSessionId(request);
        if (user == null) {
            guestCartService.clearCart(sessionId);
        } else {
            userCartService.clearCart(user);
        }
        return ResponseEntity.noContent().build();
    }

}