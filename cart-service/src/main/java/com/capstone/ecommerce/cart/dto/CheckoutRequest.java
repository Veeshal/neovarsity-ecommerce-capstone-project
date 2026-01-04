package com.capstone.ecommerce.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record CheckoutRequest(
        @NotNull Long addressId,
        // Payment method id should be 4(Stripe), 5 (Razorpay) or 6 (COD) and mention it in swagger docs
        @Schema(
                description = "Payment Method ID: 4 (Stripe), 5 (Razorpay), 6 (Cash on Delivery)",
                example = "4"
        )
        @NotNull int paymentMethodId) {
}
