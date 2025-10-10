package com.lakomka.controller;

import com.lakomka.dto.filter.FilterBoundariesDto;
import com.lakomka.repository.product.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Кастомный контроллер, для решения нестабильности создания
 * контроллера репозитория по методу с кастомным запросом @Query
 */
@RestController
public class ProductFilterController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/products/search/getFilterBoundaries")
    public FilterBoundariesDto getFilterBoundaries() {
        return productRepository.getFilterBoundaries();
    }
}
