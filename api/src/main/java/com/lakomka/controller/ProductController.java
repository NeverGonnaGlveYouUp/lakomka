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

@Slf4j
@Controller
public class ProductController {

    @Autowired
    private ProductFilterRepository productRepository;

    @ResponseBody
    @GetMapping("/products/getByFilter")
    public Page<Product> findAllByRsql(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "page") Integer page,
            @RequestParam(value = "size") Integer size
    ) {
        log.info("input findAllByRsql: search:{}, sort:{}, page:{}, size:{}", search, sort, page, size);

        Specification<Product> searchSpecification = toSpecification(search);
        Specification<Product> searchSpecificationSorted = searchSpecification.and(toSort(sort));
        Page<Product> all = productRepository.findAll(searchSpecificationSorted, PageRequest.of(page, size));

        log.info("output findAllByRsql: elements: {}, total elements: {}, total pages: {} ",
                all.getSize(), all.getTotalElements(), all.getTotalPages());
        return all;
    }
}
