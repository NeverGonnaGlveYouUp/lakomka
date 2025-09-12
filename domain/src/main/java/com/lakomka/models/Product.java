package com.lakomka.models;

import jakarta.persistence.*;

/**
 * Сущность товара
 */
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, length = 12)
    private Long id;

    /**
     * Наименование товара
     */
    @Column(name = "name", length = 80)
    private String name;

    /**
     * Наименование группы товаров к которой относится товар
     */
    @Column(name = "group", length = 50)
    private String group;

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
    @Column(name = "zn", length = 2)
    private Integer zn;

    //todo какой дефолт?
    /**
     * Признак маркируемых товаров – 0 – товар не моркируемый, 1- маркируемый товар
     */
    @Column(name = "mark")
    private boolean mark;

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

    //todo 15 или 20?
    /**
     * Штрих код товара (проверить разрядность – может надо больше например, 20)
     */
    @Column(name = "stroke", length = 15)
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
