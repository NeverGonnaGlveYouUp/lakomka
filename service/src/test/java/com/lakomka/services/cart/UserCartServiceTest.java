package com.lakomka.services.cart;

import com.lakomka.dto.CartItemDto;
import com.lakomka.models.person.BasePerson;
import com.lakomka.models.product.PersonCartItem;
import com.lakomka.models.product.Product;
import com.lakomka.repository.product.PersonCartItemRepository;
import com.lakomka.repository.product.ProductRepository;
import com.lakomka.services.DiscountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserCartService add/update/delete and retrieval methods.
 */
@ExtendWith(MockitoExtension.class)
class UserCartServiceTest {

    @Mock
    private PersonCartItemRepository personCartItemRepository;

    @Mock
    private DiscountService discountService;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private UserCartService userCartService;

    private final Long productId = 100L;
    private BasePerson user;

    @BeforeEach
    void init() {
        user = mock(BasePerson.class);
    }

    @Test
    @DisplayName("Добавляет несуществующий товар в корзину, должен вернуть null")
    void addToCart_whenProductNotFound_returnsNull() {
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertNull(userCartService.addToCart(user, productId, 1, false));
        verifyNoInteractions(personCartItemRepository);
    }

    @Test
    @DisplayName("Должен добавить новый товар в корзину")
    void addToCart_createsNewProduct_whenProductNotInCart() {
        Product newProduct = mock(Product.class);
        when(productRepository.findById(productId)).thenReturn(Optional.of(newProduct));

        when(personCartItemRepository.findAllByBasePersonAndProduct(user, newProduct)).thenReturn(Optional.empty());

        CartItemDto dto = mock(CartItemDto.class);
        when(discountService.applyToCartItemDto(any(PersonCartItem.class))).thenReturn(dto);

        CartItemDto result = userCartService.addToCart(user, productId, 3, false); // Add a new product with quantity 3

        assertSame(dto, result);

        verify(personCartItemRepository, only()).save(argThat(personCartItem ->
                personCartItem.getProduct().equals(newProduct) &&
                        personCartItem.getQuantity() == 3 &&
                        personCartItem.getBasePerson().equals(user)
        ));
    }

    @Test
    @DisplayName("Ставит кол-во у товара как 0, должно быть вызвано удаление элемента корзины")
    void addToCart_updatesExistingItem_quantityZero_triggersDelete() {
        Product product = mock(Product.class);
        PersonCartItem existing = new PersonCartItem(user, product, 4, false);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(personCartItemRepository.findAllByBasePersonAndProduct(user, product)).thenReturn(Optional.of(existing));

        CartItemDto dto = mock(CartItemDto.class);
        when(discountService.applyToCartItemDto(existing)).thenReturn(dto);

        CartItemDto result = userCartService.addToCart(user, productId, 0, false);

        assertSame(dto, result);
        verify(personCartItemRepository, times(1)).delete(existing);
        // When quantity is zero, save should not be called
        verify(personCartItemRepository, never()).save(existing);
    }

    @Test
    @DisplayName("Должен вернуть мапу количеств содержимого корзины")
    void getCartIdQuantityHashMap_returnsMapping() {
        Product p1 = mock(Product.class);
        Product p2 = mock(Product.class);
        when(p1.getId()).thenReturn(11L);
        when(p2.getId()).thenReturn(22L);

        PersonCartItem item1 = new PersonCartItem(user, p1, 2, false);
        PersonCartItem item2 = new PersonCartItem(user, p2, 5, true);

        when(personCartItemRepository.findAllByBasePerson(user)).thenReturn(List.of(item1, item2));

        HashMap<Long, Integer> map = userCartService.getCartIdQuantityHashMap(user);

        assertEquals(2, map.size());
        assertEquals(2, map.get(11L));
        assertEquals(5, map.get(22L));
    }

    @Test
    @DisplayName("Должен вернуть содержимое корзины")
    void getCart_returnsNonEmptyCart() {
        Product p1 = mock(Product.class);
        PersonCartItem item = new PersonCartItem(user, p1, 1, false);

        when(personCartItemRepository.findAllByBasePerson(user)).thenReturn(List.of(item));
        when(discountService.applyToCartItemDto(item)).thenReturn(mock(CartItemDto.class));

        Set<com.lakomka.dto.CartItemDto> cart = userCartService.getCart(user);

        assertNotNull(cart);
        assertFalse(cart.isEmpty(), "The cart should not be empty when there are items.");
    }

    @Test
    @DisplayName("Должна быть получена мапа с ценой заказа")
    void getCartSummary_returnsSummaryMap() {
        Product p1 = mock(Product.class);
        PersonCartItem item = new PersonCartItem(user, p1, 2, false);
        when(personCartItemRepository.findAllByBasePerson(user)).thenReturn(List.of(item));
        when(userCartService.getUserPrice(item)).thenReturn(new BigDecimal("100"));

        Map<String, Object> summary = userCartService.getCartSummary(user);

        assertNotNull(summary, "The summary should not be null.");
    }

    @Test
    @DisplayName("Должен удалить продукт из корзины")
    void clearCart_removesAllItems() {
        Product p1 = mock(Product.class);
        PersonCartItem item = new PersonCartItem(user, p1, 1, false);

        when(personCartItemRepository.findAllByBasePerson(user)).thenReturn(List.of(item));

        userCartService.clearCart(user);

        verify(personCartItemRepository, atLeastOnce()).delete(any(PersonCartItem.class));
    }
}
