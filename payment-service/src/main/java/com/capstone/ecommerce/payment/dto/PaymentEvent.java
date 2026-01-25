package com.capstone.ecommerce.payment.dto;

public record PaymentEvent(
        String orderId,
        String paymentId,
        PaymentEventStatus status
) {}
