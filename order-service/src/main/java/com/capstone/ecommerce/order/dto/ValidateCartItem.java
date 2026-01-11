package com.capstone.ecommerce.order.dto;

public record ValidateCartItem(
        Long product,
        int quantity
) {
}
