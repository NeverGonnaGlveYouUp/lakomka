package com.lakomka.controller;

import com.lakomka.models.product.Product;
import com.lakomka.repository.product.ProductFilterRepository;
import com.lakomka.specifications.CustomRsqlVisitor;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private ProductFilterRepository productRepository;

    @ResponseBody
    @GetMapping("/products/getByFilter")
    public List<Product> findAllByRsql(@RequestParam(value = "search") String search) {
        Node rootNode = new RSQLParser().parse(search);
        Specification<Product> spec = rootNode.accept(new CustomRsqlVisitor<Product>());
        return productRepository.findAll(spec);
    }
}
