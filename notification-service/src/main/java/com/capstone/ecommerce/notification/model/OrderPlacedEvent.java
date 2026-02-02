package com.capstone.ecommerce.notification.model;

import java.math.BigDecimal;

public record OrderPlacedEvent(
        String orderId,
        long userId,
        BigDecimal amount,
        String orderDate
) {
}
