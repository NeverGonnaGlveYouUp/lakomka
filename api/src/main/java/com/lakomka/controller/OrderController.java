package com.lakomka.controller;

import com.lakomka.dto.OrderCreationRequest;
import com.lakomka.dto.OrderDto;
import com.lakomka.models.order.Order;
import com.lakomka.models.person.BasePerson;
import com.lakomka.services.order.OrderCreationRequestService;
import com.lakomka.services.order.OrderService;
import com.lakomka.services.xml.exports.OrderExport;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;
    private final OrderCreationRequestService requestService;
    private final OrderExport orderExport;

    /**
     * Creates order for authenticated user, based on Cart content and user default properties
     *
     * @param user    user
     * @param request HttpServletRequest
     * @return OrderDTO
     */
    @PostMapping("/create-from-cart")
    public ResponseEntity<OrderDto> createOrderFromCart(
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
     * @param request              HttpServletRequest
     * @param orderCreationRequest additional details
     * @return OrderDTO
     */
    @PostMapping("/create-from-cart/with-additional-details")
    public ResponseEntity<OrderDto> createOrderFromCartWithDetails(
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

    /**
     * Return list of orders for user
     *
     * @param user    - user
     * @param request - HttpServletRequest
     * @return - page of List<OrderDTO>
     */
    @GetMapping("/list")
    public ResponseEntity<Page<OrderDto>> getOrdersByPerson(
            @AuthenticationPrincipal BasePerson user,
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.unsorted());
        Page<OrderDto> orders = orderService.getOrders(user, request, pageable);
        return ResponseEntity.ok(orders);
    }

    /**
     * Export order to Xml file on S# storage
     *
     * @param user    - user
     * @param request - HttpServletRequest
     * @return - true if success
     */
    @GetMapping("/export-to-s3")
    public ResponseEntity<?> exportXml(
            @AuthenticationPrincipal BasePerson user,
            HttpServletRequest request,
            @RequestParam long orderId
    ) {
        return ResponseEntity.ok(orderExport.safeExportXml(user, request, orderId));
    }

}
