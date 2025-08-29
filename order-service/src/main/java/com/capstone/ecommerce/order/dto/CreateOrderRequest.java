package com.capstone.ecommerce.order.dto;

public record CreateOrderRequest(
        Long userId,
        Long addressId,
        String paymentMethod,
        Cart cart
) {
}
