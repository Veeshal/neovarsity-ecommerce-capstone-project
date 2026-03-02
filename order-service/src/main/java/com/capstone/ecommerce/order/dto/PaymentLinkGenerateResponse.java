package com.capstone.ecommerce.order.dto;

public record PaymentLinkGenerateResponse(
        String link,
        long expiresAt,
        String redirectUrl,
        String paymentLinkId,
        String orderId
) {
}
