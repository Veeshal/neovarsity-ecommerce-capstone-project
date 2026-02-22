package com.capstone.ecommerce.payment.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentLinkStatus {
    CREATED("created"),
    PARTIALLY_PAID("partially_paid"),
    EXPIRED("expired"),
    CANCELLED("cancelled"),
    PAID("paid");

    private final String value;

    PaymentLinkStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static PaymentLinkStatus fromValue(String value) {
        for (PaymentLinkStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + value);
    }
}