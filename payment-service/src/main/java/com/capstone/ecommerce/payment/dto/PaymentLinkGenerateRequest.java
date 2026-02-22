package com.capstone.ecommerce.payment.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record PaymentLinkGenerateRequest(
    String gateway,
    List<Item> items,
    @NotBlank String currency,
    @NotBlank String orderId
) {
}

