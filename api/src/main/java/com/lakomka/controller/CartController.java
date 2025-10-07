package com.lakomka.controller;

import com.lakomka.models.person.BasePerson;
import com.lakomka.models.product.PersonCartItem;
import com.lakomka.models.product.Product;
import com.lakomka.services.cart.CartService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<String> addToCart(
            @AuthenticationPrincipal BasePerson user,
            @RequestBody Product cartItem,
            HttpServletRequest request,
            @RequestParam(value = "quantity", defaultValue = "1") Integer quantity) {
        
        cartService.addToCart(user, cartItem, request, quantity);
        return ResponseEntity.ok("Item added to cart");
    }

    @GetMapping("/items")
    public ResponseEntity<Set<PersonCartItem>> getCartItems(
            @AuthenticationPrincipal BasePerson user,
            HttpServletRequest request) {
        
        Set<PersonCartItem> cartItems = cartService.getCart(user, request);
        return ResponseEntity.ok(cartItems);
    }
}
