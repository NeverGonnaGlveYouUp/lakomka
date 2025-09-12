package com.lakomka.repository;

import com.lakomka.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *  Репозиторий товара
 */
public interface ProductRepository extends JpaRepository<Product, Long> {
}