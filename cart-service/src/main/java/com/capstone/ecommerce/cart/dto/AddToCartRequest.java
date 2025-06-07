package com.capstone.ecommerce.cart.dto;

public record AddToCartRequest(
        Long productId,
        int quantity,
        Long userId
) {
}
