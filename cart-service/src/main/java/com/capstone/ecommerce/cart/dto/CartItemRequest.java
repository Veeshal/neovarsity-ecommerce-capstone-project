package com.capstone.ecommerce.cart.dto;

public record CartItemRequest(
        Long cartItemId,
        Long productId,
        int quantity,
        Long userId
) {
}
