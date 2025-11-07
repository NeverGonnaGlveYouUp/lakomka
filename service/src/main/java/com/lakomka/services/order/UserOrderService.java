package com.lakomka.services.order;

import com.lakomka.dto.OrderCreationRequest;
import com.lakomka.dto.OrderDTO;
import com.lakomka.models.order.Order;
import com.lakomka.models.person.BasePerson;
import com.lakomka.models.product.PersonCartItem;
import com.lakomka.repository.order.OrderItemRepository;
import com.lakomka.repository.order.OrderRepository;
import com.lakomka.repository.person.BasePersonRepository;
import com.lakomka.repository.product.PersonCartItemRepository;
import com.lakomka.services.DiscountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserOrderService extends Common {

    private final PersonCartItemRepository cartItemRepository;

    public UserOrderService(OrderRepository orderRepository,
                            OrderItemRepository orderItemRepository,
                            DiscountService discountService,
                            PersonCartItemRepository cartItemRepository,
                            BasePersonRepository basePersonRepository) {
        super(orderRepository, orderItemRepository, discountService, basePersonRepository);
        this.cartItemRepository = cartItemRepository;
    }

    @Transactional
    public Order createOrderFromCart(BasePerson basePerson, OrderCreationRequest request) {

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

        return savedOrder;
    }

    public List<OrderDTO> getOrders(BasePerson user) {
        return getOrderRepository().findAllByBasePerson(user)
                .stream()
                .map(Order::toOrderDTO)
                .toList();
    }
}
