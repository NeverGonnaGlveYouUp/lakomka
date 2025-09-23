package com.lakomka.repository.product;

import com.lakomka.models.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *  Репозиторий товара
 */
public interface ProductRepository extends JpaRepository<Product, Long> {
}