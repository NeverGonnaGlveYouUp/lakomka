package com.lakomka.dto;

import java.math.BigDecimal;

public record OrderItemDto(
        String productName,
        String unit,
        String packag,
        boolean bitPackag,
        Integer quantity,
        BigDecimal price,
        Double weightPackag) {
}
