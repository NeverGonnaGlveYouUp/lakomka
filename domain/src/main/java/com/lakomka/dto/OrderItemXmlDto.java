package com.lakomka.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemXmlDto {

    /**
     * Уникальный индекс товара
     */
    private String productId;

    /**
     * Аритикул товара
     */
    private String art;

    /**
     * Единиц измерения количества товара
     */
    private String unit;

    /**
     * Норма упаковки товара
     */
    private String packag;

    /**
     * Цена товара
     */
    private String price;

    /**
     * Количество товара
     */
    private Integer quantity;

    /**
     * Вес товара или упаковки в зависимости от состояния bitPackag
     */
    private Double weightPackag;


    /**
     * Бит отгрузки нормами упаковок – 0 – Quantity это штуки или килограммы, 1 – Quantity это упаковки
     */
    private boolean bitPackag;

}
