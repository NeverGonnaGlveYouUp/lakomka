package com.lakomka.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductFeedDto {

    private Long id;
    private String name;
    private BigDecimal priceKons;
    private Integer cartQuantity;

    public ProductFeedDto(Long id, String name, BigDecimal priceKons) {
        this.id = id;
        this.name = name;
        this.priceKons = priceKons;
    }
}
