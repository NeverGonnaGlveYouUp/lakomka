package com.lakomka.services.cart;

import com.lakomka.models.product.PersonCartItem;
import com.lakomka.services.order.OrderCommon;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.lakomka.services.DiscountService.getPackagQuantityCoefficient;

public abstract class CartCommon {

    public abstract BigDecimal getUserPrice(PersonCartItem item);

    public Map<String, Object> makeSummary(Collection<PersonCartItem> cart) {
        // Calculate summary information
        int totalItems = cart.stream().map(PersonCartItem::getQuantity).reduce(0, Integer::sum);
        BigDecimal totalPrice = cart.stream()
                .map(item -> getUserPrice(item).multiply(BigDecimal.valueOf(getPackagQuantityCoefficient(item))))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalWeight = cart.stream()
                .map(OrderCommon::getWeightPackag)
                .map(val -> BigDecimal.valueOf(val).setScale(2, RoundingMode.HALF_UP))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Create summary object
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalItems", totalItems);
        summary.put("totalPrice", totalPrice);
        summary.put("totalWeight", totalWeight);
        return summary;
    }

}
