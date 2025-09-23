package com.lakomka.repository.product;

import com.lakomka.models.product.PersonCartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonCartItemRepository extends JpaRepository<PersonCartItem, Long> {
}