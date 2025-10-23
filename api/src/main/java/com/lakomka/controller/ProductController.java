package com.lakomka.controller;

import com.lakomka.dto.ProductDto;
import com.lakomka.dto.ProductFeedDto;
import com.lakomka.models.person.BasePerson;
import com.lakomka.models.product.Product;
import com.lakomka.repository.product.ProductFilterRepository;
import com.lakomka.repository.product.ProductRepository;
import com.lakomka.services.cart.CartService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

import static io.github.perplexhub.rsql.RSQLJPASupport.toSort;
import static io.github.perplexhub.rsql.RSQLJPASupport.toSpecification;

@Slf4j
@Controller
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private ProductFilterRepository productFilterRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartService cartService;

    @ResponseBody
    @GetMapping("/products/getByFilter")
    public Page<ProductFeedDto> findAllByRsql(
            HttpServletRequest request,
            @AuthenticationPrincipal BasePerson user,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "page") Integer page,
            @RequestParam(value = "size") Integer size
    ) {
        log.info("input findAllByRsql: search:{}, sort:{}, page:{}, size:{}", search, sort, page, size);

        HashMap<Long, Integer> cart = cartService.getCartIdQuantityHashMap(user, request);
        Set<Long> cartKeys = cart.keySet();

        Specification<Product> searchSpecification = toSpecification(search);
        Specification<Product> searchSpecificationSorted = searchSpecification.and(toSort(sort));
        Page<ProductFeedDto> all = productFilterRepository.findAll(searchSpecificationSorted, PageRequest.of(page, size)).map(Product::toProductFeedDto);

        all.getContent().stream()
                .filter(productFeedDto -> cartKeys.contains(productFeedDto.getId()))
                .forEach(productFeedDto -> productFeedDto.setCartQuantity(cart.get(productFeedDto.getId())));

        log.info("output findAllByRsql: elements: {}, total elements: {}, total pages: {} ",
                all.getSize(), all.getTotalElements(), all.getTotalPages());
        return all;
    }

    @ResponseBody
    @GetMapping("/product")
    public ResponseEntity<?> findById(
            HttpServletRequest request,
            @AuthenticationPrincipal BasePerson user,
            @RequestParam(value = "id") Long id
    ) {

        Optional<Product> product = productFilterRepository.findById(id);
        if (product.isPresent()) {

            ProductDto productDto = product.get().toProductDto();
            HashMap<Long, Integer> cart = cartService.getCartIdQuantityHashMap(user, request);
            Optional.ofNullable(cart.get(productDto.getId())).ifPresent(productDto::setCartQuantity);

            return ResponseEntity.ok(productDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @ResponseBody
    @GetMapping("/randProductsByGroup")
    public List<ProductFeedDto> findRandomByProductGroup(
            HttpServletRequest request,
            @AuthenticationPrincipal BasePerson user,
            @RequestParam(value = "id") Long productId,
            @RequestParam(value = "quantity") Integer quantity
    ) {

        HashMap<Long, Integer> cart = cartService.getCartIdQuantityHashMap(user, request);
        Set<Long> cartKeys = cart.keySet();

        List<ProductFeedDto> randomByProductGroup = productRepository.findRandomByProductGroup(productId, quantity);

        randomByProductGroup.stream()
                .filter(productFeedDto -> cartKeys.contains(productFeedDto.getId()))
                .forEach(productFeedDto -> productFeedDto.setCartQuantity(cart.get(productFeedDto.getId())));

        return randomByProductGroup;
    }

}
