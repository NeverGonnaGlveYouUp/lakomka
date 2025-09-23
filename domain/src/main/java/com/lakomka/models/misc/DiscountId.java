package com.lakomka.models.misc;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serial;
import java.io.Serializable;

/**
 * уникальный индекс покупателя
 * наименование группы товаров к которой относится товар
 * уникальный индекс товара
 */
@Embeddable
public class DiscountId implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "product_id")
    Long productId;

    @Column(name = "product_group_id")
    Long productGroupId;

    @Column(name = "base_person_id")
    Long basePersonId;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getProductGroupId() {
        return productGroupId;
    }

    public void setProductGroupId(Long productGroupId) {
        this.productGroupId = productGroupId;
    }

    public Long getBasePersonId() {
        return basePersonId;
    }

    public void setBasePersonId(Long basePersonId) {
        this.basePersonId = basePersonId;
    }
}
