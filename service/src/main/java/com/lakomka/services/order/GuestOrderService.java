package com.lakomka.services.order;

import com.lakomka.configs.SystemUserDatabaseInitializer;
import com.lakomka.dto.OrderCreationRequest;
import com.lakomka.dto.OrderDto;
import com.lakomka.models.order.Order;
import com.lakomka.models.person.BasePerson;
import com.lakomka.models.product.PersonCartItem;
import com.lakomka.repository.order.OrderItemRepository;
import com.lakomka.repository.order.OrderRepository;
import com.lakomka.repository.person.BasePersonRepository;
import com.lakomka.services.DiscountService;
import com.lakomka.services.cart.GuestCartService;
import com.lakomka.services.xml.exports.OrderExport;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class GuestOrderService extends Common {

    private final GuestCartService guestCartService;

    public GuestOrderService(OrderRepository orderRepository,
                             OrderItemRepository orderItemRepository,
                             DiscountService discountService,
                             GuestCartService guestCartService,
                             BasePersonRepository basePersonRepository,
                             OrderExport orderExport
    ) {
        super(orderRepository, orderItemRepository, discountService, basePersonRepository, orderExport);
        this.guestCartService = guestCartService;
    }

    @Transactional
    public Order createOrderFromCart(String currentSessionId, OrderCreationRequest request) {

        // Get cart items for this session
        List<PersonCartItem> cartItems = guestCartService.getCartRaw(currentSessionId);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty for session " + currentSessionId);
        }

        // get system user
        Optional<BasePerson> systemUser = getBasePersonRepository().findByLogin(SystemUserDatabaseInitializer.SYSTEM_USER);
        if (systemUser.isEmpty()) {
            throw new RuntimeException("Not found system user");
        }

        Order savedOrder = makeOrder(systemUser.get(), request, cartItems, currentSessionId);

        // Clear cart after savedOrder creation
        guestCartService.clearCart(currentSessionId);

        return savedOrder;
    }

    public List<OrderDto> getOrders(String currentSessionId) {
        return getOrderRepository().findAllByGuest(currentSessionId)
                .stream()
                .map(Order::toOrderDTO)
                .toList();
    }
}
