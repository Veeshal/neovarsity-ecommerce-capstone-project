package com.capstone.ecommerce.order.controller;

import com.capstone.ecommerce.order.dto.CreateOrderRequest;
import com.capstone.ecommerce.order.dto.OrderDto;
import com.capstone.ecommerce.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @PostMapping
    public OrderDto placeOrder(@RequestBody CreateOrderRequest request) {
        var order = orderService.placeOrder(request.userId(), request.addressId(), request.paymentMethod(), request.cart());
        return OrderDto.from(order);
    }

    @GetMapping("{orderId}")
    public OrderDto getOrder(@AuthenticationPrincipal Jwt jwt, @PathVariable("orderId") String orderId) {
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

    private Long getUserIdFromToken(Jwt jwt) {
        log.info("jwt {}", jwt);

//        jwt.getHeaders().forEach((s, o) -> log.info("Headers >> {}: {}", s, o));
//        jwt.getClaims().forEach((s, o) -> log.info("Claims >> {}: {}", s, o));

        return  (Long) jwt.getClaims().get("userId");
    }
}
