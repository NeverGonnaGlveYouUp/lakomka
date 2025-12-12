package com.lakomka.services.order;

import com.lakomka.dto.OrderCreationRequest;
import com.lakomka.dto.OrderDto;
import com.lakomka.dto.OrderItemDto;
import com.lakomka.models.order.Order;
import com.lakomka.models.order.OrderItem;
import com.lakomka.models.person.BasePerson;
import com.lakomka.models.person.PersonEnum;
import com.lakomka.models.product.PersonCartItem;
import com.lakomka.repository.order.OrderItemRepository;
import com.lakomka.repository.order.OrderRepository;
import com.lakomka.repository.person.BasePersonRepository;
import com.lakomka.services.DiscountService;
import com.lakomka.services.xml.exports.OrderExport;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

import static java.util.Objects.nonNull;

@Getter
@RequiredArgsConstructor
public abstract class OrderCommon {

    @Getter
    private final PersonEnum personEnum;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final DiscountService discountService;
    private final BasePersonRepository basePersonRepository;
    private final OrderExport orderExport;

    @Transactional
    public abstract OrderDto createOrderFromCart(BasePerson basePerson, String currentSessionId, OrderCreationRequest request);
    public abstract Page<OrderDto> getOrdersPage(BasePerson user, String currentSessionId, HttpServletRequest request, Pageable pageable);
    public abstract List<OrderItemDto> getOrderContent(BasePerson user, String currentSessionId, Long orderId);

    public Order makeOrder(
            BasePerson basePerson,
            OrderCreationRequest request,
            List<PersonCartItem> cartItems,
            String currentSessionId
    ) {
        Order order = new Order();
        order.setBasePerson(basePerson);
        order.setDateTimeOrder(Instant.now());
        order.setSumWeight(0);
        order.setSumOrder(BigDecimal.ZERO);
        order.setAdressDelivery(nonNull(request) && nonNull(request.getAddressDelivery()) ? request.getAddressDelivery() : "");
        order.setDateDelivery(nonNull(request) && nonNull(request.getDateDelivery()) ? request.getDateDelivery() : getCurrentDateWithOffset(1));
        order.setBitAccPrint(nonNull(request) && request.isBitAccPrint());
        order.setBitSertifPrint(nonNull(request) && request.isBitSertifPrint());
        order.setDatePay(getDatePay(request, basePerson));
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

            Double weightPackag = getWeightPackag(cartItem);
            BigDecimal appliedToPrice = discountService.applyToPrice(cartItem);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setBasePerson(basePerson);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setUnit(cartItem.getProduct().getUnit());
            orderItem.setPackag(Optional.ofNullable(cartItem.getProduct().getPackag()).map(Object::toString).orElse("-"));
            orderItem.setBitPackag(cartItem.isBitPackag());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(appliedToPrice);
            orderItem.setWeightPackag(weightPackag);

            // Calculate total sum and weight
            BigDecimal itemTotal = appliedToPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalSum = totalSum.add(itemTotal);
            totalWeight += (int) (cartItem.getQuantity() * weightPackag);

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

    private Date getCurrentDateWithOffset(Integer datePayOffset) {
        Date currentDate = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_MONTH, datePayOffset);

        return calendar.getTime();
    }

    /**
     * Дата оплаты – если наличные, то DatePay= DateDelivery, если б/нал и в карточке стоит отсрочка,
     * то DatePay= DateDelivery+Количество дней отсрочки
     *
     * @return дата
     */
    private Date getDatePay(OrderCreationRequest request, BasePerson basePerson) {
        return nonNull(request) && nonNull(request.getDateDelivery()) && request.isPayVid() ?
                request.getDateDelivery() :
                getCurrentDateWithOffset(basePerson.getJPerson().getDay());
    }

    /**
     * Вес товара или упаковки в зависимости от состояния bitPackag
     * Бит отгрузки нормами упаковок – 0 – Quantity это штуки или килограммы,
     * 1 – Quantity это упаковки
     */
    public static Double getWeightPackag(PersonCartItem cartItem) {
        Integer weight = cartItem.getProduct().getWeight();
        if (cartItem.isBitPackag()) {
            // упаковки
            return cartItem.getQuantity() * (weight / 1000.) * cartItem.getProduct().getPackag();
        } else {
            // штуки или килограммы
            return cartItem.getQuantity() * (weight / 1000.);
        }
    }

    public static PersonEnum getPersonKind(
            BasePerson user
    ) {
        return user == null ? PersonEnum.GUEST : PersonEnum.JPERSON;
    }
}
