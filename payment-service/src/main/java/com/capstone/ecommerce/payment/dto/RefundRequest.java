package com.capstone.ecommerce.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RefundRequest(
        @NotBlank String gateway,
        @NotBlank String orderId,
        String paymentId,
        @NotNull BigDecimal amount,
        String reason,
        @JsonProperty("isFullRefund") boolean isFullRefund
) {
}
