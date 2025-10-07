package com.lakomka.controller;

import com.lakomka.models.product.Product;
import com.lakomka.repository.product.ProductFilterRepository;
import com.lakomka.specifications.CustomRsqlVisitor;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            @RequestParam(value = "sort") String sort) {;
        Node rootNode = new RSQLParser().parse(search);
        Specification<Product> spec = rootNode.accept(new CustomRsqlVisitor<>());
        Pattern pattern = Pattern.compile("(\\w+);\\s*(asc|desc)");
        Matcher matcher = pattern.matcher(sort);
        Page<Product> products;
        if (matcher.find()) {
            String firstGroup = matcher.group(1);
            String secondGroup = matcher.group(2);
            if (Objects.equals(secondGroup, "asc")) {
                products = productRepository.findAll(spec, PageRequest.of(page, size, Sort.by(firstGroup).ascending()));
            } else if (Objects.equals(secondGroup, "desc")) {
                products = productRepository.findAll(spec, PageRequest.of(page, size, Sort.by(firstGroup).descending()));
            } else {
                products = productRepository.findAll(spec, PageRequest.of(page, size));
            }
        } else {
            products = productRepository.findAll(spec, PageRequest.of(page, size));
        }
        return products;
    }
}
