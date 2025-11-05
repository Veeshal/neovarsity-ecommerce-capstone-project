package com.capstone.ecommerce.payment.dto;

public record Item(
    String itemName,
    int quantity,
    double price
) {
}
