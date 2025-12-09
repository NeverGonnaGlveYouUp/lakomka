package com.lakomka.services.cart;

import com.lakomka.dto.CartItemDto;
import com.lakomka.models.product.PersonCartItem;
import com.lakomka.models.product.Product;
import com.lakomka.repository.product.ProductRepository;
import com.lakomka.services.DiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GuestCartService extends CartCommon {

    private final ProductRepository productRepository;
    private final DiscountService discountService;

    private final Map<String, Set<PersonCartItem>> sessionCarts = new ConcurrentHashMap<>();

    public CartItemDto addToCart(
            String sessionId,
            Long productId,
            Integer quantity,
            boolean bitPackag
    ) {
        Optional<Product> product = productRepository.findById(productId);

        if (product.isEmpty()) {
            return null;
        } else {

            PersonCartItem cartItem = new PersonCartItem(null, product.get(), quantity, bitPackag);
            Set<PersonCartItem> cart = sessionCarts.computeIfAbsent(sessionId, k -> new CopyOnWriteArraySet<>());

            if (quantity == 0) {
                cart.remove(cartItem);
            } else if (cart.stream().anyMatch(personCartItem -> personCartItem.equals(cartItem))) {
                cart.stream().
                        filter(personCartItem -> personCartItem.getProduct().getId().equals(productId)).
                        forEach(personCartItem -> personCartItem.setQuantity(quantity));
            } else {
                cart.add(cartItem);
            }

            return discountService.applyToCartItemDto(cartItem);
        }
    }

    public HashMap<Long, Integer> getCartIdQuantityHashMap(String sessionId) {
        return (HashMap<Long, Integer>)
                sessionCarts.getOrDefault(sessionId, new HashSet<>())
                        .stream()
                        .collect(Collectors.toMap(
                                personCartItem -> personCartItem.getProduct().getId(),
                                PersonCartItem::getQuantity));
    }

    public Set<CartItemDto> getCart(String sessionId) {
        return sessionCarts.getOrDefault(sessionId, new HashSet<>())
                .stream()
                .map(discountService::applyToCartItemDto)
                .collect(Collectors.toSet());
    }

    public List<PersonCartItem> getCartRaw(String sessionId) {
        return sessionCarts.getOrDefault(sessionId, new HashSet<>()).stream().toList();
    }

    public Map<String, Object> getCartSummary(String sessionId) {
        Set<PersonCartItem> cart = sessionCarts.getOrDefault(sessionId, new HashSet<>());
        if (cart == null || cart.isEmpty()) {
            return null;
        }
        return makeSummary(cart);
    }

    @Override
    public BigDecimal getUserPrice(PersonCartItem item) {
        return item.getProduct().priceSelector(DiscountService.DEFAULT_BASE_PRICE);
    }

    public void clearCart(String sessionId) {
        // Очищаем корзину в сессии
        sessionCarts.remove(sessionId);
    }

}

