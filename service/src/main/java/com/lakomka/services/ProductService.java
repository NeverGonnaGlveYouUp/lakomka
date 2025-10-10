package com.lakomka.services;

import com.lakomka.models.product.Product;
import com.lakomka.models.product.ProductGroup;
import com.lakomka.repository.product.ProductFilterRepository;
import com.lakomka.repository.product.ProductGroupRepository;
import com.lakomka.specifications.CustomRsqlVisitor;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ProductService {

    @Autowired
    private ProductFilterRepository productRepository;

    public Page<Product> findAllByRsql(
            String search,
            String sort,
            Integer page,
            Integer size
    ) {
        Pattern pattern = Pattern.compile("(\\w+);\\s*(asc|desc)");
        Specification<Product> spec = null;
        Matcher matcher = null;
        Page<Product> products;

        if (search != null) {
            Node rootNode = new RSQLParser().parse(search);
            spec = rootNode.accept(new CustomRsqlVisitor<>());
        }
        if (sort != null) {
            matcher = pattern.matcher(sort);
        }

        if (matcher != null && matcher.find()) {
            String firstGroup = matcher.group(1);
            String secondGroup = matcher.group(2);
            if (spec != null) {
                if (Objects.equals(secondGroup, "asc")) {
                    products = productRepository.findAll(spec, PageRequest.of(page, size, Sort.by(firstGroup).ascending()));
                } else if (Objects.equals(secondGroup, "desc")) {
                    products = productRepository.findAll(spec, PageRequest.of(page, size, Sort.by(firstGroup).descending()));
                } else {
                    products = productRepository.findAll(spec, PageRequest.of(page, size));
                }
            } else {
                if (Objects.equals(secondGroup, "asc")) {
                    products = productRepository.findAll(PageRequest.of(page, size, Sort.by(firstGroup).ascending()));
                } else if (Objects.equals(secondGroup, "desc")) {
                    products = productRepository.findAll(PageRequest.of(page, size, Sort.by(firstGroup).descending()));
                } else {
                    products = productRepository.findAll(PageRequest.of(page, size));
                }
            }
        } else {
            if (spec != null) {
                products = productRepository.findAll(spec, PageRequest.of(page, size));
            } else {
                products = productRepository.findAll(PageRequest.of(page, size));
            }
        }
        return products;
    }
}
