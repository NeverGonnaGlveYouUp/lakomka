package com.lakomka.dto;

import java.math.BigDecimal;
import java.util.Date;

/**
 * DTO for {@link com.lakomka.models.order.Order}
 */
public record OrderDto(Long id, String status, String datePay, String dateDelivery,
                       BigDecimal sumOrder) {
}