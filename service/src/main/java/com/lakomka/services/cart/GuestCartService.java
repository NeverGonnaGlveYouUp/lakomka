package com.lakomka.services.cart;

import com.lakomka.models.product.PersonCartItem;
import com.lakomka.models.product.Product;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GuestCartService {

    private final Map<String, Set<PersonCartItem>> sessionCarts = new ConcurrentHashMap<>();

    public void addToCart(String sessionId, Product cartItem, Integer quantity) {
        Set<PersonCartItem> cart = sessionCarts.computeIfAbsent(sessionId, k -> new HashSet<>());
        cart.add(new PersonCartItem(null, cartItem, quantity));
    }

    public Set<PersonCartItem> getCart(String sessionId) {
        return sessionCarts.getOrDefault(sessionId, new HashSet<>());
    }
}

