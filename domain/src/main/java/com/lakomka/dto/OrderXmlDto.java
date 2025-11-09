package com.lakomka.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO for xml export for {@link com.lakomka.models.order.Order}
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderXmlDto {
    String orderNumber;

    String personName; // SystemUser for guest order

    String contact;
    String email;
    String telephone;
    String prim;
    String addressDelivery;

    String datePay;
    String dateDelivery;
    String dateTimeOrder;

    BigDecimal sumOrder;
    Integer sumWeight;

    boolean bitAccPrint;
    boolean bitSertifPrint;

}