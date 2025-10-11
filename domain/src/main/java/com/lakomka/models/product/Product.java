package com.lakomka.models.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lakomka.models.misc.Discount;
import com.lakomka.models.order.OrderItem;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * Сущность товара
 */
@Table
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, precision = 12)
    private Long id;

    @JsonIgnore
    @OneToMany(mappedBy = "product")
    private Set<PersonCartItem> personCartItems = new HashSet<>();

    @JsonIgnore
    @OneToOne(mappedBy = "product")
    private OrderItem orderItem;

    @JsonIgnore
    @OneToMany(mappedBy = "product")
    private Set<Discount> discounts = new HashSet<>();

    /**
     * Наименование товара
     */
    @Column(name = "name", length = 80)
    private String name;

    /**
     * Наименование группы товаров к которой относится товар
     */
    @Column(name = "product_group")
    private String productGroup;

    /**
     * Артикул товара
     */
    @Column(name = "article", length = 50)
    private String article;

    /**
     * Единица измерения количества товара (шт, кг, короб и т.д)
     */
    @Column(name = "unit", length = 20)
    private String unit;

    /**
     * Краткое описание единицы измерения количества товара. Например, Unit – шт, UnitVid – стеклобанка 900мл
     */
    @Column(name = "unit_vid", length = 30)
    private String unitVid;

    /**
     * Норма упаковки товара (количество товара в одной упаковке)
     */
    @Column(name = "packag", length = 50)
    private Integer packag;

    /**
     *  priceOpt1, priceOpt2, priceNal, priceKons варианты цен
     */
    @Column(name = "price_opt_1")
    private BigDecimal priceOpt1;

    @Column(name = "price_opt_2")
    private BigDecimal priceOpt2;

    @Column(name = "price_nal")
    private BigDecimal priceNal;

    @Column(name = "price_kons")
    private BigDecimal priceKons;

    /**
     * Вес товара в граммах/милилитрах
     */
    @Column(name = "weight", length = 12)
    private Integer weight;

    /**
     * Количество товара - поле предусматриваем, но не заполняем
     */
    @Column(name = "quantity", length = 12)
    private Integer quantity;

    /**
     * Значимость товара – одна или две цифры
     */
    @Column(name = "zn")
    private Integer zn;

    /**
     * Признак маркируемых товаров – 0 – товар не моркируемый, 1- маркируемый товар
     */
    @JsonIgnore
    @Column(name = "mark")
    private boolean mark = false;

    /**
     * Название группы СКЮ или торговой марки
     */
    @Column(name = "sku", length = 50)
    private String sku;

    /**
     * Краткое наименование производителя товара
     */
    @Column(name = "worker", length = 80)
    private String worker;

    /**
     * Штрих код товара (проверить разрядность – может надо больше например, 20)
     */
    @Column(name = "stroke", length = 13)
    private String stroke;

    /**
     * Страна происхождения товара
     */
    @Column(name = "country", length = 50)
    private String country;

    /**
     * Краткое описание или характеристики товара
     */
    @Column(name = "description")
    private String description;

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", priceKons=" + priceKons +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<PersonCartItem> getPersonCartItems() {
        return personCartItems;
    }

    public void setPersonCartItems(Set<PersonCartItem> personCartItems) {
        this.personCartItems = personCartItems;
    }

    public OrderItem getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }

    public Set<Discount> getDiscounts() {
        return discounts;
    }

    public void setDiscounts(Set<Discount> discounts) {
        this.discounts = discounts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return productGroup;
    }

    public void setGroup(String productGroup) {
        this.productGroup = productGroup;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getUnitVid() {
        return unitVid;
    }

    public void setUnitVid(String unitVid) {
        this.unitVid = unitVid;
    }

    public Integer getPackag() {
        return packag;
    }

    public void setPackag(Integer packag) {
        this.packag = packag;
    }

    public BigDecimal getPriceOpt1() {
        return priceOpt1;
    }

    public void setPriceOpt1(BigDecimal priceOpt1) {
        this.priceOpt1 = priceOpt1;
    }

    public BigDecimal getPriceOpt2() {
        return priceOpt2;
    }

    public void setPriceOpt2(BigDecimal priceOpt2) {
        this.priceOpt2 = priceOpt2;
    }

    public BigDecimal getPriceNal() {
        return priceNal;
    }

    public void setPriceNal(BigDecimal priceNal) {
        this.priceNal = priceNal;
    }

    public BigDecimal getPriceKons() {
        return priceKons;
    }

    public void setPriceKons(BigDecimal priceKons) {
        this.priceKons = priceKons;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getZn() {
        return zn;
    }

    public void setZn(Integer zn) {
        this.zn = zn;
    }

    public boolean isMark() {
        return mark;
    }

    public void setMark(boolean mark) {
        this.mark = mark;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getWorker() {
        return worker;
    }

    public void setWorker(String worker) {
        this.worker = worker;
    }

    public String getStroke() {
        return stroke;
    }

    public void setStroke(String stroke) {
        this.stroke = stroke;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
