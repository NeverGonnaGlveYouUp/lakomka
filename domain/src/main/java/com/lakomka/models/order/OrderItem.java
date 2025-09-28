package com.lakomka.models.order;

import com.lakomka.models.person.BasePerson;
import com.lakomka.models.product.Product;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Table
@Entity
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, precision = 12)
    private Long id;

    /**
     * Уникальный индекс заказа
     */
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Order order;

    /**
     * Уникальный индекс покупателя
     */
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "base_person_id", referencedColumnName = "id")
    private BasePerson basePerson;

    /**
     * Уникальный индекс товара
     */
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    /**
     * Единиц измерения количества товара
     */
    @Column(name = "unit", length = 20, nullable = false)
    private String unit;

    /**
     * Норма упаковки товара
     */
    @Column(name = "packag", length = 50, nullable = false)
    private String packag;

    /**
     * Бит отгрузки нормами упаковок – 0 – Quantity это штуки или килограммы, 1 – Quantity это упаковки
     */
    @Column(name = "bit_packag", nullable = false)
    private boolean bitPackag = false;

    /**
     * Количество товара
     */
    @Column(name = "quantity", precision = 12, nullable = false)
    private Integer quantity;

    /**
     * Цена товара
     */
    @Column(name = "price", nullable = false, length = 10, scale = 2)
    private BigDecimal price;

    /**
     * Вес товара или упаковки в зависимости от состояния bitPackag
     */
    @Column(name = "weight_packag", precision = 12, nullable = false)
    private Integer weightPackag;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public BasePerson getBasePerson() {
        return basePerson;
    }

    public void setBasePerson(BasePerson basePerson) {
        this.basePerson = basePerson;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getPackag() {
        return packag;
    }

    public void setPackag(String packag) {
        this.packag = packag;
    }

    public boolean isBitPackag() {
        return bitPackag;
    }

    public void setBitPackag(boolean bitPackag) {
        this.bitPackag = bitPackag;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getWeightPackag() {
        return weightPackag;
    }

    public void setWeightPackag(Integer weightPackag) {
        this.weightPackag = weightPackag;
    }
}
