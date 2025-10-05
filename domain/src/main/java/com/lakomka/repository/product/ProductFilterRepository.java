package com.lakomka.repository.product;

import com.lakomka.models.product.Product;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductFilterRepository extends JpaSpecificationExecutor<Product>, CrudRepository<Product, Long> {
}