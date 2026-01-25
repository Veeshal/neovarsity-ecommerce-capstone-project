package com.capstone.ecommerce.order.entity;

public record PaymentEvent(
        String orderId,
        String paymentId,
        PaymentEventStatus status
) {}
