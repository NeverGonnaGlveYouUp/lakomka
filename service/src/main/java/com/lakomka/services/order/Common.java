package com.lakomka.services.order;

import com.lakomka.dto.OrderCreationRequest;
import com.lakomka.models.order.Order;
import com.lakomka.models.order.OrderItem;
import com.lakomka.models.person.BasePerson;
import com.lakomka.models.product.PersonCartItem;
import com.lakomka.repository.order.OrderItemRepository;
import com.lakomka.repository.order.OrderRepository;
import com.lakomka.repository.person.BasePersonRepository;
import com.lakomka.services.DiscountService;
import com.lakomka.services.xml.OrderExport;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.nonNull;

@Getter
@RequiredArgsConstructor
public abstract class Common {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final DiscountService discountService;
    private final BasePersonRepository basePersonRepository;
    private final OrderExport orderExport;

    public Order makeOrder(BasePerson basePerson, OrderCreationRequest request, List<PersonCartItem> cartItems, String currentSessionId) {

        // Create the order
        Order order = new Order();
        order.setBasePerson(basePerson);
        order.setDateTimeOrder(Instant.now());
        order.setSumWeight(0);
        order.setSumOrder(BigDecimal.ZERO);
        order.setAdressDelivery(nonNull(request) && nonNull(request.getAddressDelivery()) ? request.getAddressDelivery() : "");
        order.setDateDelivery(nonNull(request) && nonNull(request.getDateDelivery()) ? request.getDateDelivery() : new Date());
        order.setBitAccPrint(nonNull(request) && request.isBitAccPrint());
        order.setBitSertifPrint(nonNull(request) && request.isBitSertifPrint());
        order.setDatePay(getDatePay(request));
        order.setEmail(nonNull(request) && nonNull(request.getEmail()) ? request.getEmail() : "");
        order.setTelephone(nonNull(request) && nonNull(request.getTelephone()) ? request.getTelephone() : "");
        order.setContact(nonNull(request) && nonNull(request.getContact()) ? request.getContact() : "");
        order.setPrim(nonNull(request) && nonNull(request.getPrim()) ? request.getPrim() : "");
        order.setGuest(currentSessionId);

        // Save order to get ID
        order = orderRepository.save(order);

        // Create order items from cart items
        BigDecimal totalSum = BigDecimal.ZERO;
        int totalWeight = 0;

        List<OrderItem> newItems = new ArrayList<>();

        for (PersonCartItem cartItem : cartItems) {

            boolean bitPackag = getBitPackag(cartItem);
            Integer weightPackag = getWeightPackag(cartItem, bitPackag);
            BigDecimal appliedToPrice = discountService.applyToPrice(cartItem);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setBasePerson(basePerson);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setUnit(cartItem.getProduct().getUnit());
            orderItem.setPackag(Optional.ofNullable(cartItem.getProduct().getPackag()).map(Object::toString).orElse("-"));
            orderItem.setBitPackag(false); // Default value
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(appliedToPrice);
            orderItem.setWeightPackag(weightPackag);

            // Calculate total sum and weight
            BigDecimal itemTotal = appliedToPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalSum = totalSum.add(itemTotal);
            totalWeight += cartItem.getQuantity() * weightPackag;

            // Save order item
            orderItemRepository.save(orderItem);

            newItems.add(orderItem);

        }

        // Update order with calculated totals
        order.setSumOrder(totalSum);
        order.setSumWeight(totalWeight);

        String exportedFileName = orderExport.safeExportXml(order, newItems);
        order.setExportedFileName(exportedFileName);

        orderRepository.save(order);

        return order;
    }

    /**
     * Дата оплаты – если наличные, то DatePay= DateDelivery, если б/нал и в карточке стоит отсрочка,
     * то DatePay= DateDelivery+Количество дней отсрочки
     *
     * @return дата
     */
    private Date getDatePay(OrderCreationRequest request) {
        //todo !
        return nonNull(request) && nonNull(request.getDateDelivery()) ? request.getDateDelivery() : new Date();
    }

    /**
     * Вес товара или упаковки в зависимости от состояния bitPackag
     * Бит отгрузки нормами упаковок – 0 – Quantity это штуки или килограммы,
     * 1 – Quantity это упаковки
     */
    // todo !
    Integer getWeightPackag(PersonCartItem cartItem, boolean bitPackag) {
        if (bitPackag) {
            // упаковки
        } else {
            // штуки или килограммы
        }

        Integer weight = cartItem.getProduct().getWeight();
        return cartItem.getQuantity() * weight;

    }

    /**
     * Бит отгрузки нормами упаковок – 0 – Quantity это штуки или килограммы,
     * 1 – Quantity это упаковки
     */
    // todo !
    boolean getBitPackag(PersonCartItem cartItem) {
        return false;
    }
}
