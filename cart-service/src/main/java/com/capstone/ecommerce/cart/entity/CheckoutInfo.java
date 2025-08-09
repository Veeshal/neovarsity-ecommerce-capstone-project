package com.capstone.ecommerce.cart.entity;

public record CheckoutInfo(
        String orderId,
        Long userId,
        Long addressId,
        int paymentMethodId,
        String paymentLink
) {}
