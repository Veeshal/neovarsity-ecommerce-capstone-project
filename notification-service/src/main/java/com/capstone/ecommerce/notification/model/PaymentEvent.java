package com.capstone.ecommerce.notification.model;

public record PaymentEvent(
        String orderId,
        String paymentId,
        String refundId,
        PaymentEventStatus status
) {}
