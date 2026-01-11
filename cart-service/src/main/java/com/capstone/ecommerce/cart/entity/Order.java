package com.capstone.ecommerce.cart.entity;

import java.math.BigDecimal;

public record Order(
        String orderId,
        int orderStatus,
        Long userId,
        Long addressId,
        BigDecimal totalAmount,
        String paymentLink
) {
}
