package com.lakomka.services.cart;

import com.lakomka.dto.CartItemDto;
import com.lakomka.models.person.BasePerson;
import com.lakomka.repository.product.PersonCartItemRepository;
import com.lakomka.repository.product.ProductRepository;
import com.lakomka.services.DiscountService;
import com.lakomka.utils.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CartService delegation, moveGuestCartToUserCart and clearCart behaviors.
 */
@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    private GuestCartService guestCartService;

    private UserCartService userCartService;

    @Mock
    private SessionUtil sessionUtil;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private CartService cartService;

    private final Long productId = 1L;
    private final Integer quantity = 2;
    private final boolean bitPackag = false;

    @BeforeEach
    void init() {
        guestCartService = new GuestCartService(mock(DiscountService.class), mock(ProductRepository.class));
        userCartService = new UserCartService(mock(DiscountService.class), mock(ProductRepository.class), mock(PersonCartItemRepository.class));
        cartService = new CartService(guestCartService, userCartService, sessionUtil, List.of());
    }

    @Test
    @DisplayName("Добавление товара в корзину: делегируется к гостю, если пользователь равен null")
    void addToCart_delegatesToGuestWhenUserNull() {
        String sessionId = "sess-123";
        when(sessionUtil.getCurrentSessionId(request)).thenReturn(sessionId);
        CartItemDto dto = mock(CartItemDto.class);
        when(guestCartService.addToCart(null, sessionId, productId, quantity, bitPackag)).thenReturn(dto);

        CartItemDto result = cartService.addToCart(null, productId, request, quantity, bitPackag);

        assertSame(dto, result);
        verify(guestCartService, only()).addToCart(null, sessionId, productId, quantity, bitPackag);
        verifyNoInteractions(userCartService);
    }

    @Test
    @DisplayName("Добавление товара в корзину: делегируется к пользователю, если пользователь не равен null")
    void addToCart_delegatesToUserWhenUserNotNull() {
        BasePerson user = mock(BasePerson.class);
        CartItemDto dto = mock(CartItemDto.class);
        when(userCartService.addToCart(user, null, productId, quantity, bitPackag)).thenReturn(dto);

        CartItemDto result = cartService.addToCart(user, productId, request, quantity, bitPackag);

        assertSame(dto, result);
        verify(userCartService, times(1)).addToCart(user, null, productId, quantity, bitPackag);
        verifyNoInteractions(guestCartService);
    }

    @Test
    @DisplayName("Перенос гостевой корзины в корзину пользователя: переносит и очищает гостевую корзину")
    void moveGuestCartToUserCart_transfersAndClearsGuestCart() {
        BasePerson user = mock(BasePerson.class);
        String sessionId = "sess-transfer";
        HashMap<Long, Integer> guestCart = new HashMap<>();
        guestCart.put(10L, 1);
        guestCart.put(20L, 3);

        when(sessionUtil.getCurrentSessionId(request)).thenReturn(sessionId);
        when(guestCartService.getCartIdQuantityHashMap(null, sessionId)).thenReturn(guestCart);

        cartService.moveGuestCartToUserCart(user, request);

        // Verify userCartService.addToCart invoked for each entry
        verify(userCartService, times(1)).addToCart(user, null, 10L, 1, false);
        verify(userCartService, times(1)).addToCart(user, null, 20L, 3, false);

        // Verify guest cart cleared
        verify(guestCartService, times(1)).clearCart(null, sessionId);
    }

    @Test
    @DisplayName("Очистка корзины: поведение для гостя и пользователя")
    void clearCart_guestAndUserBehaviors() {
        String sessionId = "sess-clear";
        when(sessionUtil.getCurrentSessionId(request)).thenReturn(sessionId);

        // guest
        HttpStatus guestResp = cartService.clearCart(null, request);
        assertEquals(HttpStatus.NO_CONTENT, guestResp);
        verify(guestCartService, times(1)).clearCart(null, sessionId);

        // user
        BasePerson user = mock(BasePerson.class);
        HttpStatus userResp = cartService.clearCart(user, request);
        assertEquals(HttpStatus.NO_CONTENT, userResp);
        verify(userCartService, times(1)).clearCart(user, null);
    }
}