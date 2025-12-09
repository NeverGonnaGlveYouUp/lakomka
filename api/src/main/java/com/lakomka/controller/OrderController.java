package com.lakomka.controller;

import com.lakomka.dto.OrderCreationRequest;
import com.lakomka.dto.OrderDto;
import com.lakomka.dto.OrderItemDto;
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

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;
    private final OrderCreationRequestService requestService;
    private final OrderExport orderExport;

    /**
     * Creates order with details for guest or authenticated user
     *
     * @param user                 user
     * @param request              HttpServletRequest
     * @param orderCreationRequest details
     * @return OrderDTO
     */
    @PostMapping("/create-from-cart")
    public ResponseEntity<OrderDto> createOrderFromCartWithDetails(
            @AuthenticationPrincipal BasePerson user,
            HttpServletRequest request,
            @RequestBody OrderCreationRequest orderCreationRequest) {
        try {
            log.info("createOrderFromCartWithDetails: user: {}, orderCreationRequest: {}", Optional.ofNullable(user).map(BasePerson::getLogin).orElse(null), orderCreationRequest.toString());
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
     * Return list of order content for user
     *
     * @param user    - user
     * @return - List<OrderItemDto>
     */
    @GetMapping("/order-content")
    public ResponseEntity<List<OrderItemDto>> getOrderContentByPersonAndOrder(
            @AuthenticationPrincipal BasePerson user,
            @RequestParam(value = "orderId") Long orderId
    ) {
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        List<OrderItemDto> orderContent = orderService.getOrderContent(user, orderId);
        return ResponseEntity.ok(orderContent);
    }


    /**
     * Export order to Xml file on S3 storage
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
