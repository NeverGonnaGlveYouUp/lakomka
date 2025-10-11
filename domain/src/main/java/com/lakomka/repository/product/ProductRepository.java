package com.lakomka.repository.product;

import com.lakomka.dto.filter.FilterBoundariesDto;
import com.lakomka.models.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Репозиторий товара
 */
public interface ProductRepository extends JpaRepository<Product, Long>{

    @Query(nativeQuery = true,
            value = "SELECT \n" +
                    "    MAX(GREATEST(price_kons, price_nal, price_opt_1, price_opt_2))::INTEGER AS max_price,\n" +
                    "    MIN(LEAST(price_kons, price_nal, price_opt_1, price_opt_2))::INTEGER AS min_price,\n" +
                    "    MAX(weight) AS max_weight,\n" +
                    "    MIN(weight) AS min_weight,\n" +
                    "    (SELECT STRING_AGG(DISTINCT worker, ', ') FROM product)::VARCHAR AS distinct_worker,\n" +
                    "    (SELECT STRING_AGG(DISTINCT country, ', ') FROM product)::VARCHAR AS distinct_countries,\n" +
                    "    (SELECT STRING_AGG(DISTINCT product_group, ', ') FROM product)::VARCHAR AS distinct_product_groups \n" +
                    "FROM \n" +
                    "    product;")
    FilterBoundariesDto getFilterBoundaries();

}