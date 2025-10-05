package com.lakomka.dto.filter;

public class FilterBoundariesDto {

    private Integer maxPrice;
    private Integer minPrice;
    private Integer maxMass;
    private Integer minMass;
    private String countries;
    private String workers;
    private String productGroups;

    public FilterBoundariesDto(Integer maxPrice, Integer minPrice, Integer maxMass, Integer minMass, String worker, String country, String productGroupSet) {
        this.maxPrice = maxPrice;
        this.minPrice = minPrice;
        this.maxMass = maxMass;
        this.minMass = minMass;
        this.workers = worker;
        this.countries = country;
        this.productGroups = productGroupSet;
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

    public String getProductGroups() {
        return productGroups;
    }

    public void setProductGroups(String productGroups) {
        this.productGroups = productGroups;
    }

    public String getWorkers() {
        return workers;
    }

    public void setWorkers(String workers) {
        this.workers = workers;
    }

    public String getCountries() {
        return countries;
    }

    public void setCountries(String countries) {
        this.countries = countries;
    }
}
