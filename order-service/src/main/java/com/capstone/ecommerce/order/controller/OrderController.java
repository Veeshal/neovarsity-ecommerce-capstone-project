package com.capstone.ecommerce.order.controller;

import com.capstone.ecommerce.order.dto.CreateOrderRequest;
import com.capstone.ecommerce.order.dto.OrderDto;
import com.capstone.ecommerce.order.dto.OrderItemDto;
import com.capstone.ecommerce.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("v1/orders")
public class OrderController {

    private final OrderService orderService;

    @Value("${ecom.claims.userId}")
    private String USER_ID_CLAIM;

    @Value("${ecom.claims.email}")
    private String EMAIL_CLAIM;

    @Value("${ecom.claims.role}")
    private String ROLE_CLAIM;

    @PostMapping
    public OrderDto initiateOrder(@AuthenticationPrincipal Jwt jwt,
                                  @RequestBody CreateOrderRequest request) {
        var userId = getUserIdFromToken(jwt);
        var order = orderService.initiateOrder(
                userId,
                request.addressId(),
                request.paymentMethodId(),
                request.cart());

        return OrderDto.from(order);
    }

    @GetMapping("{orderId}")
    public OrderDto getOrder(@PathVariable("orderId") String orderId,
                             @AuthenticationPrincipal Jwt jwt) {
        var userId = getUserIdFromToken(jwt);
        var order = orderService.getOrder(userId, orderId);
        return OrderDto.from(order);
    }


    @GetMapping
    public List<OrderDto> getOrderHistory(@AuthenticationPrincipal Jwt jwt,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        var userId = getUserIdFromToken(jwt);
        var orders = orderService.getOrderHistory(userId, page, size);
        return orders.map(OrderDto::from).toList();
    }

    @GetMapping("{orderId}/items")
    public List<OrderItemDto> getOrderItems(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("orderId") String orderId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        var userId = getUserIdFromToken(jwt);
        var orderItems = orderService.getOrderItems(userId, orderId, page, size);
        return orderItems.stream().map(OrderItemDto::from).toList();
    }

    private Long getUserIdFromToken(Jwt jwt) {
        return jwt.getClaim(USER_ID_CLAIM);
    }
}
