package com.capstone.ecommerce.cart.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus {
    PROCESSING(1, "PROCESSING", "Order is being processed"),
    CONFIRMED(2, "CONFIRMED", "Order has been confirmed"),
    SHIPPED(3, "SHIPPED", "Order has been shipped"),
    DELIVERED(4, "DELIVERED", "Order has been delivered"),
    CANCELLED(5, "CANCELLED", "Order has been cancelled"),;


    private final int id;
    private final String code;
    private final String description;

}
