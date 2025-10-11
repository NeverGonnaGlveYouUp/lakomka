package com.lakomka.models.misc;

import com.lakomka.models.person.JPerson;
import com.lakomka.models.product.Product;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Date;

@Table
@Entity
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, precision = 12)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "j_person_id")
    private JPerson jPerson;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    /**
     * Признак скидки/наценки – 0 скидка, 1- наценка
     */
    @Column(name = "bit_discount")
    private boolean bitDiscount = false;

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
    private boolean bitStop = false;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public JPerson getjPerson() {
        return jPerson;
    }

    public void setjPerson(JPerson jPerson) {
        this.jPerson = jPerson;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
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
