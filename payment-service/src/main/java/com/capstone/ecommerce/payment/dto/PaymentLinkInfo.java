package com.capstone.ecommerce.payment.dto;

public record PaymentLinkInfo(
    String link,
    long expiresAt,
    String redirectUrl,
    String paymentLinkId,
    String orderId
) {
}
