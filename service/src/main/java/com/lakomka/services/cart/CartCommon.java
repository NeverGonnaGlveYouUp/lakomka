package com.lakomka.services.cart;

import com.lakomka.dto.CartItemDto;
import com.lakomka.models.person.BasePerson;
import com.lakomka.models.person.PersonEnum;
import com.lakomka.models.product.PersonCartItem;
import com.lakomka.repository.product.ProductRepository;
import com.lakomka.services.DiscountService;
import com.lakomka.services.order.OrderCommon;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.lakomka.services.DiscountService.getPackagQuantityCoefficient;

@RequiredArgsConstructor
public abstract class CartCommon {

    protected final PersonEnum personEnum;
    protected final DiscountService discountService;
    protected final ProductRepository productRepository;

    public abstract BigDecimal getUserPrice(PersonCartItem item);
    public abstract CartItemDto addToCart(BasePerson user, String sessionId, Long productId, Integer quantity, boolean bitPackag);
    public abstract HashMap<Long, Integer> getCartIdQuantityHashMap(BasePerson user, String sessionId);
    public abstract Set<CartItemDto> getCart(BasePerson user, String sessionId);
    public abstract Map<String, Object> getCartSummary(BasePerson user, String sessionId);
    public abstract void clearCart(BasePerson user, String sessionId);

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
