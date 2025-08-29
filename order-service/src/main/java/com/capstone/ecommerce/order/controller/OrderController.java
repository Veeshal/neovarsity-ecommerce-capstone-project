package com.capstone.ecommerce.order.controller;

import com.capstone.ecommerce.order.dto.CreateOrderRequest;
import com.capstone.ecommerce.order.dto.OrderDto;
import com.capstone.ecommerce.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public OrderDto getOrder(@PathVariable("orderId") Long orderId) {
        log.info("Order status retrieved successfully.");
        var order = orderService.getOrder(orderId);
        return OrderDto.from(order);
    }


    @GetMapping("/history")
    public List<OrderDto> getOrderHistory() {
        log.info("Order status retrieved successfully.");
        var orders = orderService.getOrderHistory();
        return orders.stream().map(OrderDto::from).toList();
    }

}
