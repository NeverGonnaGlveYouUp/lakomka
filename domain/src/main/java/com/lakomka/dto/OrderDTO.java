package com.lakomka.dto;

import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * DTO for {@link com.lakomka.models.order.Order}
 */
@Value
public class OrderDTO {
    Long id;

    String contact;
    String email;
    String telephone;
    String prim;
    String adressDelivery;

    Date datePay;
    Date dateDelivery;
    LocalDateTime dateTimeOrder;

    BigDecimal sumOrder;
    Integer sumWeight;

    boolean bitAccPrint;
    boolean bitSertifPrint;
}