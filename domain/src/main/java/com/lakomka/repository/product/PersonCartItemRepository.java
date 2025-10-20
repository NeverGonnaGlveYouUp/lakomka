package com.lakomka.repository.product;

import com.lakomka.models.person.BasePerson;
import com.lakomka.models.product.PersonCartItem;
import com.lakomka.models.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PersonCartItemRepository extends JpaRepository<PersonCartItem, Long> {
    Optional<PersonCartItem> findAllByBasePersonAndProduct(BasePerson basePerson, Product product);

    List<PersonCartItem> findAllByBasePerson(BasePerson basePerson);
}