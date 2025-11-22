package com.lakomka.services.order;

import com.lakomka.dto.OrderCreationRequest;
import com.lakomka.dto.OrderDto;
import com.lakomka.models.order.Order;
import com.lakomka.models.person.BasePerson;
import com.lakomka.utils.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final UserOrderService userOrderService;
    private final GuestOrderService guestOrderService;
    private final SessionUtil sessionUtil;

    public Order createOrderFromCart(BasePerson user, HttpServletRequest request, OrderCreationRequest orderCreationRequest) {
        if (user == null) {
            String currentSessionId = sessionUtil.getCurrentSessionId(request);
            return guestOrderService.createOrderFromCart(currentSessionId, orderCreationRequest);
        } else {
            return userOrderService.createOrderFromCart(user, orderCreationRequest);
        }
    }

    public Page<OrderDto> getOrders(BasePerson user, HttpServletRequest request, Pageable pageable) {
        List<OrderDto> orders;
        long total;
        if (user == null) {
            // get order list for guest user only for existing session
            String currentSessionId = sessionUtil.getCurrentSessionId(request);
            orders = guestOrderService.getOrders(currentSessionId, pageable);
            total = guestOrderService.countOrders(currentSessionId);
        } else {
            orders = userOrderService.getOrders(user, pageable);
            total = userOrderService.countOrders(user);
        }
        return new PageImpl<>(orders, pageable, total);
    }
}