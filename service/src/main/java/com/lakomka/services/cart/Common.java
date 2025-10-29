package com.lakomka.services.cart;

import com.lakomka.models.product.PersonCartItem;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class Common {

    public abstract BigDecimal getUserPrice(PersonCartItem item);

    public Map<String, Object> makeSummary(Collection<PersonCartItem> cart) {
        // Calculate summary information
        int totalItems = cart.stream().map(PersonCartItem::getQuantity).reduce(0, Integer::sum);
        double totalPrice = cart.stream()
                .map(item -> getUserPrice(item).multiply(BigDecimal.valueOf(item.getQuantity())))
                .mapToDouble(BigDecimal::doubleValue)
                .sum();
        int totalWeight = cart.stream().map(pci -> pci.getProduct().getWeight()).reduce(0, Integer::sum);

        // Create summary object
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalItems", totalItems);
        summary.put("totalPrice", totalPrice);
        summary.put("totalWeight", totalWeight);
        return summary;
    }

}
