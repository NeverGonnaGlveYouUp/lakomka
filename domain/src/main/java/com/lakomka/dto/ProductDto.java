package com.lakomka.dto;

import java.math.BigDecimal;

public class ProductDto {

    private Long id;
    private String name;
    private String article;
    private String unit;
    private String unitVid;
    private Integer packag;
    private BigDecimal price;
    private Integer weight;
    private Integer quantity;
    private Integer zn;
    private String sku;
    private String worker;
    private String stroke;
    private String country;
    private String description;

    public ProductDto(Long id, String name, String article, String unit, String unitVid, Integer packag, BigDecimal priceKons, Integer weight, Integer quantity, Integer zn, String sku, String worker, String stroke, String country, String description) {
        this.id = id;
        this.name = name;
        this.article = article;
        this.unit = unit;
        this.unitVid = unitVid;
        this.packag = packag;
        this.price = priceKons;
        this.weight = weight;
        this.quantity = quantity;
        this.zn = zn;
        this.sku = sku;
        this.worker = worker;
        this.stroke = stroke;
        this.country = country;
        this.description = description;
    }

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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
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
