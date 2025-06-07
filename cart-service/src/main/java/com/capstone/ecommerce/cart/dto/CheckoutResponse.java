package com.capstone.ecommerce.cart.dto;

import com.capstone.ecommerce.cart.entity.CheckoutInfo;

public record CheckoutResponse(
        CheckoutInfo info
) {
}
