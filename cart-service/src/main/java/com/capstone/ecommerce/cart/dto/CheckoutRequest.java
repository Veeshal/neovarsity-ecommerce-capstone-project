package com.capstone.ecommerce.cart.dto;

import jakarta.validation.constraints.NotNull;

public record CheckoutRequest(
        @NotNull Long userId,
        @NotNull Long addressId,
        @NotNull int paymentMethodId) {
}
