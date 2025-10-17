package com.lakomka.controller;

import com.lakomka.dto.FilterBoundariesDto;
import com.lakomka.repository.product.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ProductFilterController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/products/search/getFilterBoundaries")
    public FilterBoundariesDto getFilterBoundaries() {
        return productRepository.getFilterBoundaries();
    }
}
