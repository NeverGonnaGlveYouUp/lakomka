package com.lakomka.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FilterBoundariesDto {

    private Integer maxPrice;
    private Integer minPrice;
    private Integer maxMass;
    private Integer minMass;
    private String workers;
    private String countries;
    private String productGroups;

}
