package com.lakomka.repository.product;

import com.lakomka.dto.filter.FilterBoundariesDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Кастомный контроллер, для решения нестабильности создания
 * контроллера репозитория по методу с кастомным запросом @Query
 */
@RestController
public class ProductFilterController {

    private final ProductRepository productRepository;

    public ProductFilterController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping("/products/search/getFilterBoundaries")
    public FilterBoundariesDto getFilterBoundaries() {
        return productRepository.getFilterBoundaries();
    }
}