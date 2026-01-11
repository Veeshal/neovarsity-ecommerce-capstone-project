package com.capstone.ecommerce.product.dto;

public record ValidateCartItem(
        Long productId,
        int quantity
) {
}
