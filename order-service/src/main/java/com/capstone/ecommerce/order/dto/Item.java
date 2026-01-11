package com.capstone.ecommerce.order.dto;

public record Item(
    String itemName,
    int quantity,
    double price
) {
}
