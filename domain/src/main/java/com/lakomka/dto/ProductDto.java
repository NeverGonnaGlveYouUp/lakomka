package com.lakomka.dto;

import java.math.BigDecimal;

public class ProductDto {

    private Long id;
    private String name;
    private String article;
    private String unit;
    private String unitVid;
    private Integer packag;
    private BigDecimal priceOpt1;
    private BigDecimal priceOpt2;
    private BigDecimal priceNal;
    private BigDecimal priceKons;
    private Integer weight;
    private Integer quantity;
    private Integer zn;
    private String sku;
    private String worker;
    private String stroke;
    private String country;
    private String description;

    public ProductDto(Long id, String name, String article, String unit, String unitVid, Integer packag, BigDecimal priceOpt1, BigDecimal priceOpt2, BigDecimal priceNal, BigDecimal priceKons, Integer weight, Integer quantity, Integer zn, String sku, String worker, String stroke, String country, String description) {
        this.id = id;
        this.name = name;
        this.article = article;
        this.unit = unit;
        this.unitVid = unitVid;
        this.packag = packag;
        this.priceOpt1 = priceOpt1;
        this.priceOpt2 = priceOpt2;
        this.priceNal = priceNal;
        this.priceKons = priceKons;
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
