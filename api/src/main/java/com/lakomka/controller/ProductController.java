package com.lakomka.controller;

import com.lakomka.models.product.Product;
import com.lakomka.services.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    @ResponseBody
    @GetMapping("/products/getByFilter")
    public Page<Product> findAllByRsql(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "page") Integer page,
            @RequestParam(value = "size") Integer size
    ) {
        log.info("input findAllByRsql: search:{}, sort:{}, page:{}, size:{}", search, sort, page, size);
        Page<Product> allByRsql = productService.findAllByRsql(
                search,
                sort,
                page,
                size
        );
        allByRsql.get().forEach(product -> log.info(product.toString()));
        log.info("output findAllByRsql: elements: {}, total elements: {}, total pages: {}\n, ",allByRsql.getSize(), allByRsql.getTotalElements(), allByRsql.getTotalPages());
        return allByRsql;
    }
}
