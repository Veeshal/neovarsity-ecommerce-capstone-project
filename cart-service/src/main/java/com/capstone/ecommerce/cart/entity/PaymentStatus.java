package com.capstone.ecommerce.cart.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PaymentStatus {

    PENDING(1, "PENDING", "Payment is pending"),
    COMPLETED(2, "COMPLETED", "Payment has been completed"),
    FAILED(2, "FAILED", "Payment has failed"),
    REFUNDED(3, "REFUNDED", "Payment has been refunded");

    private final int id;
    private final String code;
    private final String description;

}
