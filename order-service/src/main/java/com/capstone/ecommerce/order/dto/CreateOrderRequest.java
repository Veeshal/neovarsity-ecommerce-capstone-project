package com.capstone.ecommerce.order.dto;

public record CreateOrderRequest(
        Long addressId,
        int paymentMethodId,
        Cart cart
) {
}
