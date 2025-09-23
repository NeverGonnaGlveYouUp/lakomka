package com.lakomka.services.cart;

import com.lakomka.models.person.BasePerson;
import com.lakomka.models.product.PersonCartItem;
import com.lakomka.models.product.Product;
import com.lakomka.repository.person.BasePersonRepository;
import com.lakomka.repository.product.PersonCartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserCartService {

    @Autowired
    private BasePersonRepository userRepository;

    @Autowired
    private PersonCartItemRepository personCartItemRepository;

    public void addToCart(Long userId, Product productToCart, Integer quantity) {
        BasePerson basePerson = userRepository.findById(userId).orElseThrow();
        personCartItemRepository.save(new PersonCartItem(basePerson, productToCart, quantity));
    }

    public Set<PersonCartItem> getCart(Long userId) {
        return userRepository.findById(userId).orElseThrow().getCart();
    }
}
