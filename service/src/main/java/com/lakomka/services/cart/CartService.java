package com.lakomka.services.cart;

import com.lakomka.dto.CartItemDto;
import com.lakomka.models.person.BasePerson;
import com.lakomka.utils.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CartService {

    private final GuestCartService guestCartService;
    private final UserCartService userCartService;
    private final SessionUtil sessionUtil;

    public CartItemDto addToCart(
            BasePerson user,
            Long productId,
            HttpServletRequest request,
            Integer quantity,
            boolean bitPackag
    ) {
        if (user == null) {
            return guestCartService.addToCart(sessionUtil.getCurrentSessionId(request), productId, quantity, bitPackag);
        } else {
            return userCartService.addToCart(user, productId, quantity, bitPackag);
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


    public Set<CartItemDto> getCart(
            BasePerson user,
            HttpServletRequest request
    ) {
        if (user == null) {
            return guestCartService.getCart(sessionUtil.getCurrentSessionId(request));
        } else {
            return userCartService.getCart(user);
        }
    }

    public Map<String, Object> getCartSummary(
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
                userCartService.addToCart(user, entry.getKey(), entry.getValue(), false);
            }

            // Очищаем анонимную корзину
            guestCartService.clearCart(sessionId);
        }
    }

    public HttpStatus clearCart(BasePerson user, HttpServletRequest request) {
        String sessionId = sessionUtil.getCurrentSessionId(request);
        if (user == null) {
            guestCartService.clearCart(sessionId);
        } else {
            userCartService.clearCart(user);
        }
        return HttpStatus.NO_CONTENT;
    }

}