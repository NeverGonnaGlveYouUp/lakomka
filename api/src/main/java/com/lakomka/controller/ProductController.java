package com.lakomka.controller;

import com.lakomka.models.product.Product;
import com.lakomka.repository.product.ProductFilterRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import static io.github.perplexhub.rsql.RSQLJPASupport.toSort;
import static io.github.perplexhub.rsql.RSQLJPASupport.toSpecification;

@Controller
public class ProductController {

    @Autowired
    private ProductFilterRepository productRepository;

    @ResponseBody
    @GetMapping("/products/getByFilter")
    public Page<Product> findAllByRsql(
            @RequestParam(value = "search") String search,
            @RequestParam(value = "page") Integer page,
            @RequestParam(value = "size") Integer size,
            @RequestParam(value = "sort") String sort) {

        Specification<Product> searchSpecification = toSpecification(search);
        Specification<Product> searchSpecificationSorted = searchSpecification.and(toSort(sort));
        return productRepository.findAll(searchSpecificationSorted, PageRequest.of(page, size));

    }
}
