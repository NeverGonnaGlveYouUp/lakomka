package com.lakomka.services.cart;

import com.lakomka.dto.CartItemDto;
import com.lakomka.models.person.BasePerson;
import com.lakomka.models.person.PersonEnum;
import com.lakomka.utils.SessionUtil;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.lakomka.services.cart.CartCommon.getPersonKind;

@Service
@RequiredArgsConstructor
public class CartService {

    private final GuestCartService guestCartService;
    private final UserCartService userCartService;
    private final SessionUtil sessionUtil;
    private final List<CartCommon> cartServices;
    private final Map<PersonEnum, CartCommon> personServiceMap = new HashMap<>();

    @PostConstruct
    public void init() {
        for (CartCommon personService : cartServices) {
            personServiceMap.put(personService.getPersonEnum(), personService);
        }
    }

    public CartItemDto addToCart(
            BasePerson user,
            Long productId,
            HttpServletRequest request,
            Integer quantity,
            boolean bitPackag
    ) {
        return personServiceMap.get(getPersonKind(user)).addToCart(
                user,
                sessionUtil.getCurrentSessionId(request),
                productId,
                quantity,
                bitPackag
        );
    }

    public HashMap<Long, Integer> getCartIdQuantityHashMap(
            BasePerson user,
            HttpServletRequest request
    ) {
        return personServiceMap.get(getPersonKind(user)).getCartIdQuantityHashMap(
                user,
                sessionUtil.getCurrentSessionId(request)
        );
    }


    public Set<CartItemDto> getCart(
            BasePerson user,
            HttpServletRequest request
    ) {
        return personServiceMap.get(getPersonKind(user)).getCart(
                user,
                sessionUtil.getCurrentSessionId(request)
        );
    }

    public Map<String, Object> getCartSummary(
            BasePerson user,
            HttpServletRequest request
    ) {
        return personServiceMap.get(getPersonKind(user)).getCartSummary(
                user,
                sessionUtil.getCurrentSessionId(request)
        );
    }

    public void moveGuestCartToUserCart(BasePerson user, HttpServletRequest request) {
        String sessionId = sessionUtil.getCurrentSessionId(request);
        if (sessionId != null) {
            // Получаем содержимое анонимной корзины
            HashMap<Long, Integer> guestCart = guestCartService.getCartIdQuantityHashMap(null, sessionId);

            // Переносим товары в пользовательскую корзину
            for (HashMap.Entry<Long, Integer> entry : guestCart.entrySet()) {
                userCartService.addToCart(user, null, entry.getKey(), entry.getValue(), false);
            }

            // Очищаем анонимную корзину
            guestCartService.clearCart(null ,sessionId);
        }
    }

    public HttpStatus clearCart(BasePerson user, HttpServletRequest request) {
        personServiceMap.get(getPersonKind(user)).clearCart(
                user,
                sessionUtil.getCurrentSessionId(request)
        );
        return HttpStatus.NO_CONTENT;
    }

}