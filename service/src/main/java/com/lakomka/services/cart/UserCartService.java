package com.lakomka.services.cart;

import com.lakomka.dto.CartItemDto;
import com.lakomka.models.person.BasePerson;
import com.lakomka.models.product.PersonCartItem;
import com.lakomka.models.product.Product;
import com.lakomka.repository.product.PersonCartItemRepository;
import com.lakomka.repository.product.ProductRepository;
import com.lakomka.services.DiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserCartService extends Common {

    private final PersonCartItemRepository personCartItemRepository;
    private final DiscountService discountService;

    @Autowired
    private ProductRepository productRepository;

    public ResponseEntity<CartItemDto> addToCart(BasePerson user, Long productId, Integer quantity) {
        return productRepository.findById(productId)
                .map(product -> updateCart(user, quantity, product))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private ResponseEntity<CartItemDto> updateCart(BasePerson user, Integer quantity, Product product) {
        return personCartItemRepository.findAllByBasePersonAndProduct(user, product)
                .map(cartItem -> updateExistingItem(cartItem, quantity))
                .orElseGet(() -> addNewItem(user, product, quantity));
    }

    private ResponseEntity<CartItemDto> updateExistingItem(PersonCartItem cartItem, Integer quantity) {
        if (quantity == 0) {
            personCartItemRepository.delete(cartItem);
            cartItem.setQuantity(quantity);
            return ResponseEntity.ok().body(discountService.apply(cartItem));
        } else {
            cartItem.setQuantity(quantity);
            personCartItemRepository.save(cartItem);
            return createResponseEntity(cartItem);
        }
    }

    private ResponseEntity<CartItemDto> addNewItem(BasePerson user, Product product, Integer quantity) {
        PersonCartItem newItem = new PersonCartItem(user, product, quantity);
        personCartItemRepository.save(newItem);
        return createResponseEntity(newItem);
    }

    private ResponseEntity<CartItemDto> createResponseEntity(PersonCartItem cartItem) {
        return ResponseEntity.ok(discountService.apply(cartItem));
    }

    public HashMap<Long, Integer> getCartIdQuantityHashMap(BasePerson user) {
        return (HashMap<Long, Integer>)
                personCartItemRepository.findAllByBasePerson(user)
                        .stream()
                        .collect(Collectors.toMap(
                                personCartItem -> personCartItem.getProduct().getId(),
                                PersonCartItem::getQuantity));
    }

    public ResponseEntity<Set<CartItemDto>> getCart(BasePerson user) {
        Set<CartItemDto> cartItems =
                personCartItemRepository.findAllByBasePerson(user)
                        .stream()
                        .map(discountService::apply)
                        .collect(Collectors.toSet());
        return ResponseEntity.ok(cartItems);
    }

    public ResponseEntity<?> getCartSummary(BasePerson user) {
        try {
            List<PersonCartItem> cart = personCartItemRepository.findAllByBasePerson(user);
            if (cart == null || cart.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(makeSummary(cart));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public BigDecimal getUserPrice(PersonCartItem item) {
        return discountService.applyToPrice(item);
    }

    public void clearCart(BasePerson user) {
        personCartItemRepository
                .findAllByBasePerson(user)
                .forEach(personCartItemRepository::delete);
    }
}
