package com.capstone.ecommerce.order.dto;

public record CartItem(
        Product product,
        int quantity
) {
}
