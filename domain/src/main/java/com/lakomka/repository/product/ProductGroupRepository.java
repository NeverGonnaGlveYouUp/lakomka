package com.lakomka.repository.product;

import com.lakomka.models.product.ProductGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductGroupRepository extends JpaRepository<ProductGroup, Long> {
}