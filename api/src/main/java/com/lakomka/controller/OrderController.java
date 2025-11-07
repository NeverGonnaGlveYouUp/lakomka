package com.lakomka.controller;

import com.lakomka.dto.OrderCreationRequest;
import com.lakomka.dto.OrderDTO;
import com.lakomka.models.order.Order;
import com.lakomka.models.person.BasePerson;
import com.lakomka.services.order.OrderCreationRequestService;
import com.lakomka.services.order.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;
    private final OrderCreationRequestService requestService;

    /**
     * Creates order for authenticated user, based on Cart content and user default properties
     *
     * @param user user
     * @return OrderDTO
     */
    @PostMapping("/create-from-cart")
    public ResponseEntity<OrderDTO> createOrderFromCart(
            @AuthenticationPrincipal BasePerson user,
            HttpServletRequest request
    ) {
        try {
            OrderCreationRequest orderCreationRequest = requestService.fill(user, null);
            return this.createOrderFromCartWithDetails(user, request, orderCreationRequest);
        } catch (Exception e) {
            log.error("{}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Creates order with additional details for guest or authenticated user
     *
     * @param user                 user
     * @param orderCreationRequest additional details
     * @return OrderDTO
     */
    @PostMapping("/create-from-cart/with-additional-details")
    public ResponseEntity<OrderDTO> createOrderFromCartWithDetails(
            @AuthenticationPrincipal BasePerson user,
            HttpServletRequest request,
            @RequestBody OrderCreationRequest orderCreationRequest) {
        try {
            OrderCreationRequest orderCreationRequestEnriched = requestService.fill(user, orderCreationRequest);
            Order order = orderService.createOrderFromCart(user, request, orderCreationRequestEnriched);
            return ResponseEntity.ok(order.toOrderDTO());
        } catch (Exception e) {
            log.error("{}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<OrderDTO>> getOrdersByPerson(
            @AuthenticationPrincipal BasePerson user,
            HttpServletRequest request
    ) {
        List<OrderDTO> orders = orderService.getOrders(user, request);
        return ResponseEntity.ok(orders);
    }

}
