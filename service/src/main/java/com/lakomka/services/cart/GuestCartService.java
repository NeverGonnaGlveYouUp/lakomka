package com.lakomka.services.cart;

import com.lakomka.dto.CartItemDto;
import com.lakomka.models.person.BasePerson;
import com.lakomka.models.person.PersonEnum;
import com.lakomka.models.product.PersonCartItem;
import com.lakomka.models.product.Product;
import com.lakomka.repository.product.ProductRepository;
import com.lakomka.services.DiscountService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

@Service
public class GuestCartService extends CartCommon {

    private final Map<String, Set<PersonCartItem>> sessionCarts = new ConcurrentHashMap<>();

    public GuestCartService(DiscountService discountService, ProductRepository productRepository) {
        super(PersonEnum.GUEST, discountService, productRepository);
    }

    @Override
    public CartItemDto addToCart(
            BasePerson ignoredUser,
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

    @Override
    public HashMap<Long, Integer> getCartIdQuantityHashMap(
            BasePerson ignoredUser,
            String sessionId
    ) {
        return (HashMap<Long, Integer>)
                sessionCarts.getOrDefault(sessionId, new HashSet<>())
                        .stream()
                        .collect(Collectors.toMap(
                                personCartItem -> personCartItem.getProduct().getId(),
                                PersonCartItem::getQuantity));
    }

    @Override
    public Set<CartItemDto> getCart(BasePerson ignoredUser, String sessionId) {
        return sessionCarts.getOrDefault(sessionId, new HashSet<>())
                .stream()
                .map(discountService::applyToCartItemDto)
                .collect(Collectors.toSet());
    }

    public List<PersonCartItem> getCartRaw(BasePerson ignoredUser, String sessionId) {
        return sessionCarts.getOrDefault(sessionId, new HashSet<>()).stream().toList();
    }

    @Override
    public Map<String, Object> getCartSummary(BasePerson ignoredUser, String sessionId) {
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

    @Override
    public void clearCart(BasePerson ignoredUser, String sessionId) {
        // Очищаем корзину в сессии
        sessionCarts.remove(sessionId);
    }

}

