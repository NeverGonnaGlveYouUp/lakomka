package com.lakomka.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ProductDto {
    private Long id;
    private String name;
    private String article;
    private String unit;
    private String unitVid;
    private Integer packag;
    private BigDecimal price;
    private Integer weight;
    private Integer quantity;
    private Integer cartQuantity;
    private Integer zn;
    private String sku;
    private String worker;
    private String stroke;
    private String country;
    private String description;
}
