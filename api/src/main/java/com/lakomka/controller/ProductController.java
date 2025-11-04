package com.lakomka.controller;

import com.lakomka.dto.ProductDto;
import com.lakomka.dto.ProductFeedDto;
import com.lakomka.models.person.BasePerson;
import com.lakomka.models.product.Product;
import com.lakomka.repository.product.ProductFilterRepository;
import com.lakomka.repository.product.ProductRepository;
import com.lakomka.services.DiscountService;
import com.lakomka.services.DiscountService.Discounts;
import com.lakomka.services.cart.CartService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
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

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static io.github.perplexhub.rsql.RSQLJPASupport.toSort;
import static io.github.perplexhub.rsql.RSQLJPASupport.toSpecification;

@Slf4j
@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductController {

    private final ProductFilterRepository productFilterRepository;
    private final ProductRepository productRepository;
    private final DiscountService discountService;

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
        log.info("findAllByRsql: user: {} search:{}, sort:{}, page:{}, size:{}",
                Optional.ofNullable(user).map(BasePerson::getLogin).orElse(null),
                search, sort, page, size);

        HashMap<Long, Integer> cart = cartService.getCartIdQuantityHashMap(user, request);
        Set<Long> cartKeys = cart.keySet();

        Discounts discounts = discountService.getDiscounts(user);

        Specification<Product> searchSpecification = toSpecification(search);
        Specification<Product> searchSpecificationSorted = searchSpecification.and(toSort(sort));
        Page<ProductFeedDto> all = productFilterRepository
                .findAll(searchSpecificationSorted, PageRequest.of(page, size))
                .map(product -> discountService.apply(product, discounts).toProductFeedDto());

        all.getContent().stream()
                .filter(productFeedDto -> cartKeys.contains(productFeedDto.getId()))
                .forEach(productFeedDto -> productFeedDto.setCartQuantity(cart.get(productFeedDto.getId())));

        log.info("findAllByRsql: elements: {}, total elements: {}, total pages: {} ",
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

            Discounts discounts = discountService.getDiscounts(user);
            ProductDto productDto = discountService.apply(product.get(), discounts);
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

        // получаем набор случайных продуктов
        List<ProductFeedDto> randomByProductGroup = productRepository.findRandomByProductGroup(
                productId,
                quantity,
                discountService.getBasePrice(user).name()
        );

        // применяем скидки
        Discounts discounts = discountService.getDiscounts(user);
        List<Long> productIds = randomByProductGroup.stream().map(ProductFeedDto::getId).toList();
        List<Product> products = productRepository.findAllById(productIds);
        List<ProductFeedDto> productFeedDtoList = products.stream()
                .map(product -> discountService.apply(product, discounts).toProductFeedDto())
                .toList();

        // проставляем счетчики корзины
        productFeedDtoList.stream()
                .filter(productFeedDto -> cartKeys.contains(productFeedDto.getId()))
                .forEach(productFeedDto -> productFeedDto.setCartQuantity(cart.get(productFeedDto.getId())));

        return productFeedDtoList;
    }


}
