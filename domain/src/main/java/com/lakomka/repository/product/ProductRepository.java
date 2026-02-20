package com.lakomka.repository.product;

import com.lakomka.dto.FilterBoundariesDto;
import com.lakomka.dto.ProductFeedDto;
import com.lakomka.dto.SearchStringProductDto;
import com.lakomka.models.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Репозиторий товара
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(nativeQuery = true,
            value = """
                SELECT id, name
                FROM product
                ORDER BY name <->> :name
                LIMIT 12;
                """)
    List<SearchStringProductDto> findProductsBySearchString(@Param("name") String name);

    @Query(nativeQuery = true,
            value = """
                    SELECT
                    MAX(GREATEST(price_kons, price_nal, price_opt_1, price_opt_2))::INTEGER AS max_price,
                    MIN(LEAST(price_kons, price_nal, price_opt_1, price_opt_2))::INTEGER AS min_price,
                    MAX(weight) AS max_weight,
                    MIN(weight) AS min_weight,
                    (SELECT STRING_AGG(DISTINCT worker, ';') FROM product)::VARCHAR AS distinct_worker,
                    (SELECT STRING_AGG(DISTINCT country, ';') FROM product)::VARCHAR AS distinct_countries,
                    (SELECT STRING_AGG(DISTINCT product_group, ';') FROM product)::VARCHAR AS distinct_product_groups
                    FROM product;
                    """)
    FilterBoundariesDto getFilterBoundaries();

    List<Product> findByArticleIn(Collection<String> articles);

    default List<Product> findByArticleInSafe(Collection<String> articles) {
        List<Product> result = new ArrayList<>();

        final int BATCH_SIZE = 500;
        List<List<String>> batches = partition(new ArrayList<>(articles), BATCH_SIZE);

        for (List<String> batch : batches) {
            result.addAll(findByArticleIn(batch));
        }
        return result;
    }

    static <T> List<List<T>> partition(List<T> list, int size) {
        return IntStream.range(0, (list.size() + size - 1) / size)
                .mapToObj(i -> list.subList(i * size, Math.min((i + 1) * size, list.size())))
                .collect(Collectors.toList());
    }

    @Query(nativeQuery = true,
            value = """
                    SELECT id, name,
                    CASE
                    WHEN :level = 'KONS' THEN price_kons
                    WHEN :level = 'OPT1' THEN price_opt_1
                    WHEN :level = 'OPT2' THEN price_opt_2
                    WHEN :level = 'NAL' THEN price_nal
                    ELSE price_kons
                    END AS price, zn
                    FROM product
                    WHERE product_group =
                    (SELECT product_group
                    FROM product
                    WHERE id = :id) AND id <> :id
                    ORDER BY RANDOM() LIMIT :quantity
                    """)
    List<ProductFeedDto> findRandomByProductGroup(
            @Param("id") Long productId,
            @Param("quantity") Integer quantity,
            @Param("level") String level);

}