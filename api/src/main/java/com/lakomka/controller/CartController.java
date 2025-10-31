package com.lakomka.controller;

import com.lakomka.models.person.BasePerson;
import com.lakomka.services.cart.CartService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PutMapping("/add")
    public ResponseEntity<?> addToCart(
            @AuthenticationPrincipal BasePerson user,
            HttpServletRequest request,
            @RequestParam(value = "id") Long productId,
            @RequestParam(value = "quantity") Integer quantity
    ) {
        log.info("addToCart: user: {}, product: {}, quantity: {}", Optional.ofNullable(user).map(BasePerson::getLogin).orElse(null), productId, quantity);
        return cartService.addToCart(user, productId, request, quantity);
    }

    @GetMapping("/items")
    public ResponseEntity<?> getCartItems(
            @AuthenticationPrincipal BasePerson user,
            HttpServletRequest request
    ) {
        log.info("getCartItems: user: {}", Optional.ofNullable(user).map(BasePerson::getLogin).orElse(null));
        return cartService.getCart(user, request);
    }

    @GetMapping("/summary")
    public ResponseEntity<?> getCartSummary(
            @AuthenticationPrincipal BasePerson user,
            HttpServletRequest request
    ) {
        log.info("getCartSummary: user: {}", Optional.ofNullable(user).map(BasePerson::getLogin).orElse(null));
        return cartService.getCartSummary(user, request);
    }

    @DeleteMapping("/clear")
    public ResponseEntity<?> clearCart(
            @AuthenticationPrincipal BasePerson user,
            HttpServletRequest request
    ) {
        log.info("clearCart: user: {}", Optional.ofNullable(user).map(BasePerson::getLogin).orElse(null));
        return cartService.clearCart(user, request);
    }

    @DeleteMapping("/anonymous/clear")
    public ResponseEntity<?> clearAnonymousCart(
            HttpServletRequest request
    ) {
        log.info("clearAnonymousCart: anonymous");
        return cartService.clearCart(null, request);
    }
}
