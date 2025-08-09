package com.capstone.ecommerce.cart.dto;

import com.capstone.ecommerce.cart.entity.Cart;

public record OrderCreationRequestDto(
        Long userId,
        Long addressId,
        int paymentMethodId,
        Cart cart
) {
}
