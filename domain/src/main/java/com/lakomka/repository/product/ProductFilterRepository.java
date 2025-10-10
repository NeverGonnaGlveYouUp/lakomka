package com.lakomka.repository.product;

import com.lakomka.models.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductFilterRepository extends
        PagingAndSortingRepository<Product, Long>,
        JpaSpecificationExecutor<Product>,
        JpaRepository<Product, Long> {
    @Query(nativeQuery = true, value = "SELECT * FROM product WHERE product_group_id = :groupId")
    Page<Product> findByGroup(Specification<Product> spec, Pageable pageable, @Param("groupId") Long groupId);
    @Query(nativeQuery = true, value = "SELECT * FROM product WHERE product_group_id = :groupId")
    Page<Product> findByGroup(Pageable pageable, @Param("groupId") Long groupId);
}