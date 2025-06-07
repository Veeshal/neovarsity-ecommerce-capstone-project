package com.capstone.ecommerce.cart.entity;

public record CheckoutInfo(
        String orderId,
        Long userId,
        Long addressId,
        Long paymentMethodId,
        String paymentLink
) {}
