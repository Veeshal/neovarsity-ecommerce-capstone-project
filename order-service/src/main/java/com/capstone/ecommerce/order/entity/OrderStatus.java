package com.capstone.ecommerce.order.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum OrderStatus {
    INITIATED(1, "initiated", "Order has been initiated"),
    PLACED(2, "placed", "Order has been placed"),
    SHIPPED(3, "shipped", "Order has been shipped"),
    DELIVERED(4, "delivered", "Order has been delivered"),
    CANCELLED(5, "cancelled", "Order has been cancelled");

    private final int id;
    private final String code;
    private final String description;

    public static OrderStatus from(String code) {
        return Arrays.stream(OrderStatus.values())
                .filter(status -> status.code.equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid order status: " + code));
    }

}
