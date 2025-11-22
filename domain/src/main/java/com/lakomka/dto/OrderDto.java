package com.lakomka.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * DTO for {@link com.lakomka.models.order.Order}
 */
public record OrderDto(Long id, String contact, String email, String telephone, String prim, String addressDelivery,
                       Date datePay, Date dateDelivery, LocalDateTime dateTimeOrder, BigDecimal sumOrder,
                       Integer sumWeight, boolean bitAccPrint, boolean bitSertifPrint) {
}