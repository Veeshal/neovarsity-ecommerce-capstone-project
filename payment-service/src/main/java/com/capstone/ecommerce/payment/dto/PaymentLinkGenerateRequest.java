package com.capstone.ecommerce.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentLinkGenerateRequest(
    String gateway,
    @NotNull BigDecimal amount,
    @NotBlank String currency,
    @NotBlank String orderId
) {
}

