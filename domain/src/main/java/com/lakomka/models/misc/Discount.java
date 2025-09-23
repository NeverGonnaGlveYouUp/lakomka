package com.lakomka.models.misc;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Date;

@Table
@Entity
public class Discount {

    /**
     * уникальный индекс покупателя
     * наименование группы товаров к которой относится товар
     * уникальный индекс товара
     */
    @EmbeddedId
    private DiscountId id;

    /**
     * Признак скидки/наценки – 0 скидка, 1- наценка
     */
    @Column(name = "bit_discount")
    private boolean bitDiscount;

    /**
     * Величина сктдки/наценки
     */

    @Column(name = "rest_time", length = 12, nullable = false)
    private BigDecimal discount = new BigDecimal("0");

    /**
     * Базовая цена - PriseOpt1 или PriseOpt2 или PriseNal или PriseKons  Если Discount=0 это означает применение BasePrice иной отличной от BasePrice которая в карточке Покупателя
     */
    @Column(name = "base_price", columnDefinition = "char(4)", nullable = false)
    private String basePrice;

    /**
     * Признак запрета отгрузки – 0 разрешен, 1 - запрещен
     */
    @Column(name = "bit_stop")
    private boolean bitStop;

    /**
     * Дата начала действия правила
     */
    @Column(name = "discount_start", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateStart;

    /**
     * Дата окончания действия правила
     */
    @Column(name = "discount_end", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateEnd;

    public DiscountId getId() {
        return id;
    }

    public void setId(DiscountId id) {
        this.id = id;
    }

    public boolean isBitDiscount() {
        return bitDiscount;
    }

    public void setBitDiscount(boolean bitDiscount) {
        this.bitDiscount = bitDiscount;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public String getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(String basePrice) {
        this.basePrice = basePrice;
    }

    public boolean isBitStop() {
        return bitStop;
    }

    public void setBitStop(boolean bitStop) {
        this.bitStop = bitStop;
    }

    public Date getDateStart() {
        return dateStart;
    }

    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(Date dateEnd) {
        this.dateEnd = dateEnd;
    }
}
