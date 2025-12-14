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
import com.lakomka.repository.product.PersonCartItemRepository;
import com.lakomka.services.DiscountService;
import com.lakomka.services.xml.exports.OrderExport;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserOrderService extends OrderCommon {

    private final PersonCartItemRepository cartItemRepository;

    public UserOrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            DiscountService discountService,
            PersonCartItemRepository cartItemRepository,
            BasePersonRepository basePersonRepository,
            OrderExport orderExport
    ) {
        super(PersonEnum.JPERSON, orderRepository, orderItemRepository, discountService, basePersonRepository, orderExport);
        this.cartItemRepository = cartItemRepository;
    }

    @Override
    @Transactional
    public Optional<OrderDto> createOrderFromCart(
            BasePerson basePerson,
            String currentSessionId,
            OrderCreationRequest request
    ) {
        // Get cart items for this person
        List<PersonCartItem> cartItems = cartItemRepository.findAllByBasePerson(basePerson);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty for person " + basePerson.getLogin());
        }

        // get user
        Optional<BasePerson> attachedUser = getBasePersonRepository().findByLogin(basePerson.getLogin());
        if (attachedUser.isEmpty()) {
            throw new RuntimeException("Not found user " + basePerson.getLogin());
        }

        Order savedOrder = makeOrder(attachedUser.get(), request, cartItems, null);

        // Clear cart after savedOrder creation
        cartItemRepository.deleteAll(cartItems);

        return Optional.ofNullable(savedOrder).stream().map(Order::toOrderDTO).findFirst();
    }

    @Override
    public Page<OrderDto> getOrdersPage(
            BasePerson user,
            @Nullable String ignoredCurrentSessionId,
            HttpServletRequest request,
            Pageable pageable
    ) {
        return new PageImpl<>(getOrders(user, pageable), pageable, countOrders(user));
    }

    @Override
    public List<OrderItemDto> getOrderContent(
            BasePerson user,
            @Nullable String ignoredCurrentSessionId,
            Long orderId
    ) {
        return getOrderItemRepository().findAllByIdAndBasePerson(orderId, user)
                .stream()
                .map(OrderItem::toOrderItemDto)
                .toList();
    }

    private List<OrderDto> getOrders(BasePerson user, Pageable pageable) {
        return getOrderRepository().findAllByBasePerson(user, pageable)
                .stream()
                .map(Order::toOrderDTO)
                .toList();
    }

    private long countOrders(BasePerson user) {
        return getOrderRepository().countAllByBasePerson(user);
    }
}
