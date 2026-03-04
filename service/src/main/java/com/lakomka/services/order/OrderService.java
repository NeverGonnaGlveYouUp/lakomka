package com.lakomka.services.order;

import com.lakomka.dto.OrderCreationRequest;
import com.lakomka.dto.OrderDto;
import com.lakomka.dto.OrderItemDto;
import com.lakomka.models.person.BasePerson;
import com.lakomka.models.person.PersonEnum;
import com.lakomka.utils.SessionUtil;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.lakomka.services.order.OrderCommon.getPersonKind;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final UserOrderService userOrderService;
    private final SessionUtil sessionUtil;
    private final List<OrderCommon> orderServices;
    private final Map<PersonEnum, OrderCommon> personServiceMap = new HashMap<>();

    @PostConstruct
    public void init() {
        for (OrderCommon personService : orderServices) {
            personServiceMap.put(personService.getPersonEnum(), personService);
        }
    }

    public Optional<OrderDto> createOrderFromCart(
            BasePerson user,
            HttpServletRequest request,
            OrderCreationRequest orderCreationRequest
    ) {
        return personServiceMap.get(getPersonKind(user)).createOrderFromCart(
                user,
                sessionUtil.getCurrentSessionId(request),
                orderCreationRequest
        );
    }

    public Page<OrderDto> getOrders(
            BasePerson user,
            HttpServletRequest request,
            Pageable pageable
    ) {
        return personServiceMap.get(getPersonKind(user)).getOrdersPage(
                user,
                sessionUtil.getCurrentSessionId(request),
                request,
                pageable
        );
    }

    public List<OrderItemDto> getOrderContent(
            BasePerson user,
            HttpServletRequest request,
            Long orderId
    ) {
        return personServiceMap.get(getPersonKind(user)).getOrderContent(
                user,
                sessionUtil.getCurrentSessionId(request),
                orderId
        );
    }
}