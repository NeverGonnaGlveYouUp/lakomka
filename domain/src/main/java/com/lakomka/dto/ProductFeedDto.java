package com.lakomka.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductFeedDto {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer cartQuantity;
    private Integer zn;

    public ProductFeedDto(Long id, String name, BigDecimal price, Integer cartQuantity, Integer zn) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.cartQuantity = cartQuantity;
        this.zn = zn;
    }

    public ProductFeedDto(Long id, String name, BigDecimal price, Integer zn) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.zn = zn;
    }

}
