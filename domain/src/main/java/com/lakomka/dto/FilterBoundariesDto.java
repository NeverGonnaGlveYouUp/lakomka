package com.lakomka.dto;

import com.lakomka.models.product.ProductGroup;

import java.util.Set;

public class FilterBoundariesDto {

    private Integer maxPrice;
    private Integer minPrice;
    private Integer maxMass;
    private Integer minMass;
    private String country;
    private String worker;
    private String productGroupSet;

    public FilterBoundariesDto(Integer maxPrice, Integer minPrice, Integer maxMass, Integer minMass, String worker, String country, String productGroupSet) {
        this.maxPrice = maxPrice;
        this.minPrice = minPrice;
        this.maxMass = maxMass;
        this.minMass = minMass;
        this.worker = worker;
        this.country = country;
        this.productGroupSet = productGroupSet;
    }

    public Integer getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Integer maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Integer getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Integer minPrice) {
        this.minPrice = minPrice;
    }

    public Integer getMaxMass() {
        return maxMass;
    }

    public void setMaxMass(Integer maxMass) {
        this.maxMass = maxMass;
    }

    public Integer getMinMass() {
        return minMass;
    }

    public void setMinMass(Integer minMass) {
        this.minMass = minMass;
    }

    public String getProductGroupSet() {
        return productGroupSet;
    }

    public void setProductGroupSet(String productGroupSet) {
        this.productGroupSet = productGroupSet;
    }

    public String getWorker() {
        return worker;
    }

    public void setWorker(String worker) {
        this.worker = worker;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
