package com.lakomka.models.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lakomka.dto.ProductDto;
import com.lakomka.dto.ProductFeedDto;
import com.lakomka.models.misc.Discount;
import com.lakomka.models.order.OrderItem;
import com.lakomka.models.person.BasePrice;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * Сущность товара
 */
@Getter
@Setter
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
    private Double packag;

    /**
     * priceOpt1, priceOpt2, priceNal, priceKons варианты цен
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

    /**
     * Состав
     */
    @Column(name = "content")
    private String content;

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", priceKons=" + priceKons +
                ", priceNal=" + priceNal +
                ", priceOpt1=" + priceOpt1 +
                ", priceOpt2=" + priceOpt2 +
                '}';
    }

    public ProductFeedDto toProductFeedDto(BasePrice basePrice) {
        return new ProductFeedDto(
                id,
                name,
                priceSelector(basePrice),
                0
        );
    }

    public ProductDto toProductDto(BasePrice basePrice) {
        return new ProductDto(
                id,
                name,
                article,
                unit,
                unitVid,
                packag,
                priceSelector(basePrice),
                weight,
                quantity,
                null,
                zn,
                sku,
                worker,
                stroke,
                country,
                description,
                content,
                productGroup
        );
    }

    public BigDecimal priceSelector(BasePrice basePrice) {
        return switch (basePrice) {
            case KONS -> priceKons;
            case NAL -> priceNal;
            case OPT1 -> priceOpt1;
            case OPT2 -> priceOpt2;
        };
    }

}
