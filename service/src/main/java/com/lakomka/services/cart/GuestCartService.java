package com.lakomka.services.cart;

import com.lakomka.dto.CartItemDto;
import com.lakomka.models.product.PersonCartItem;
import com.lakomka.models.product.Product;
import com.lakomka.repository.product.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

@Service
public class GuestCartService {

    @Autowired
    private ProductRepository productRepository;

    private final Map<String, Set<PersonCartItem>> sessionCarts = new ConcurrentHashMap<>();

    public ResponseEntity<?> addToCart(
            String sessionId,
            Long productId,
            Integer quantity
    ) {
        Optional<Product> product = productRepository.findById(productId);

        if (product.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {

            PersonCartItem cartItem = new PersonCartItem(null, product.get(), quantity);
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

            return ResponseEntity.ok().body(
                    new CartItemDto(
                            product.get().getId(),
                            product.get().getName(),
                            product.get().getPriceKons().toPlainString(),
                            quantity
                    )
            );
        }
    }

    public ResponseEntity<?> getCart(String sessionId) {
        return ResponseEntity.ok().body(
                sessionCarts.getOrDefault(sessionId, new HashSet<>())
                        .stream().map(PersonCartItem::toCartItemDto)
                        .collect(Collectors.toSet())
        );
    }
}

