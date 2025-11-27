package com.lakomka.dto;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Data
public class ProductDto {
    private Long id;
    private String name;
    private String article;
    private String unit;
    private String unitVid;
    private Double packag;
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
    private String content;
    private String group;

    public ProductFeedDto toProductFeedDto() {
        return new ProductFeedDto(id, name, price, cartQuantity);
    }

}
