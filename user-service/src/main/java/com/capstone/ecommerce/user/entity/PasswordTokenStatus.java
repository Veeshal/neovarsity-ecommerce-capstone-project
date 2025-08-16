package com.capstone.ecommerce.user.entity;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PasswordTokenStatus {
    ACTIVE(0, "active", "The token is active and can be used for password reset."),
    EXPIRED(1, "expired", "The token has expired and cannot be used."),
    CONSUMED(2, "consumed", "The token has already been consumed and cannot be reused."),
    INVALID(3, "invalid", "The token is invalid and cannot be used.");

    private final int id;
    @JsonValue
    private final String code;
    private final String description;

}
