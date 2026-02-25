package com.capstone.ecommerce.order.dto;

import java.math.BigDecimal;

public record PaymentLinkGenerateRequest(
    String gateway,
    BigDecimal amount,
    String currency,
    String orderId
) {
}

