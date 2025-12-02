package com.lakomka.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO for xml import for {@link com.lakomka.models.misc.Discount}
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DiscountXmlDto {

    private Long id;

    private Long jPersonShopId;

    private Long jPersonOfficeId;

    private Long productId;

    /**
     * Признак скидки/наценки – 0 скидка, 1- наценка
     */
    private boolean bitDiscount = false;

    /**
     * Величина скидки/наценки. В процентах
     */
    private BigDecimal discount = BigDecimal.ZERO;

    /**
     * Базовая цена - PriceOpt1 или PriceOpt2 или PriceNal или PriceKons
     * Если Discount=0 это означает применение BasePrice иной отличной от BasePrice которая в карточке Покупателя
     */
    private String basePrice;

    /**
     * Признак запрета отгрузки – 0 разрешен, 1 - запрещен
     */
    private boolean bitStop = false;

    /**
     * Признак того, что скидку с этим Id надо удалить.
     */
    private boolean bitDelete = false;

    /**
     * Дата начала действия правила
     */
    private String dateStart;

    /**
     * Дата окончания действия правила
     */
    private String dateEnd;

}
