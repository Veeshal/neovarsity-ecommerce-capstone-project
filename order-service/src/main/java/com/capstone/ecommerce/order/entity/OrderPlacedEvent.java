package com.capstone.ecommerce.order.entity;

import java.math.BigDecimal;

public record OrderPlacedEvent(
        String orderId,
        long userId,
        BigDecimal amount
) {
}
