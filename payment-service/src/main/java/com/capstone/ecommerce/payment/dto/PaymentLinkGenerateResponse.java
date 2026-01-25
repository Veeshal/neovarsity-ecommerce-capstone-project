package com.capstone.ecommerce.payment.dto;

public record PaymentLinkGenerateResponse(
        String link,
        long expiresAt,
        String redirectUrl,
        String paymentId,
        String orderId
) {
}
