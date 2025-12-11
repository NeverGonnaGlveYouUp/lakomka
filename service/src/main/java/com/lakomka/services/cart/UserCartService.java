package com.lakomka.services.cart;

import com.lakomka.dto.CartItemDto;
import com.lakomka.models.person.BasePerson;
import com.lakomka.models.person.PersonEnum;
import com.lakomka.models.product.PersonCartItem;
import com.lakomka.models.product.Product;
import com.lakomka.repository.product.PersonCartItemRepository;
import com.lakomka.repository.product.ProductRepository;
import com.lakomka.services.DiscountService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserCartService extends CartCommon {

    private final PersonCartItemRepository personCartItemRepository;

    public UserCartService(DiscountService discountService, ProductRepository productRepository, PersonCartItemRepository personCartItemRepository) {
        super(PersonEnum.JPERSON, discountService, productRepository);
        this.personCartItemRepository = personCartItemRepository;
    }

    @Override
    public CartItemDto addToCart(
            BasePerson user,
            String ignoredSessionId,
            Long productId,
            Integer quantity,
            boolean bitPackag
    ) {
        return productRepository.findById(productId)
                .map(product -> updateCart(user, quantity, product, bitPackag))
                .orElse(null);
    }

    private CartItemDto updateCart(
            BasePerson user,
            Integer quantity,
            Product product,
            boolean bitPackag
    ) {
        return personCartItemRepository.findAllByBasePersonAndProduct(user, product)
                .map(cartItem -> updateExistingItem(cartItem, quantity, bitPackag))
                .orElseGet(() -> addNewItem(user, product, quantity, bitPackag));
    }

    private CartItemDto updateExistingItem(
            PersonCartItem cartItem,
            Integer quantity,
            boolean bitPackag
    ) {
        if (quantity == 0) {
            personCartItemRepository.delete(cartItem);
            cartItem.setQuantity(quantity);
            cartItem.setBitPackag(bitPackag);
        } else {
            cartItem.setQuantity(quantity);
            cartItem.setBitPackag(bitPackag);
            personCartItemRepository.save(cartItem);
        }
        return discountService.applyToCartItemDto(cartItem);
    }

    private CartItemDto addNewItem(
            BasePerson user,
            Product product,
            Integer quantity,
            boolean bitPackag
    ) {
        PersonCartItem newItem = new PersonCartItem(user, product, quantity, bitPackag);
        personCartItemRepository.save(newItem);
        return discountService.applyToCartItemDto(newItem);
    }

    @Override
    public HashMap<Long, Integer> getCartIdQuantityHashMap(
            BasePerson user,
            String sessionId
    ) {
        return (HashMap<Long, Integer>)
                personCartItemRepository.findAllByBasePerson(user)
                        .stream()
                        .collect(Collectors.toMap(
                                personCartItem -> personCartItem.getProduct().getId(),
                                PersonCartItem::getQuantity));
    }

    @Override
    public Set<CartItemDto> getCart(
            BasePerson user,
            String sessionId
    ) {
        return personCartItemRepository.findAllByBasePerson(user)
                .stream()
                .map(discountService::applyToCartItemDto)
                .collect(Collectors.toSet());
    }

    @Override
    public Map<String, Object> getCartSummary(
            BasePerson user,
            String sessionId
    ) {
        List<PersonCartItem> cart = personCartItemRepository.findAllByBasePerson(user);
        if (cart == null || cart.isEmpty()) {
            return null;
        }
        return makeSummary(cart);
    }

    @Override
    public BigDecimal getUserPrice(PersonCartItem item) {
        return discountService.applyToPrice(item);
    }

    @Override
    public void clearCart(
            BasePerson user,
            String sessionId
    ) {
        personCartItemRepository
                .findAllByBasePerson(user)
                .forEach(personCartItemRepository::delete);
    }
}
