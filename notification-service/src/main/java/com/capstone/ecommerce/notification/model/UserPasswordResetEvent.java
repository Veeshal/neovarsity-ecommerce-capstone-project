package com.capstone.ecommerce.notification.model;

public record UserPasswordResetEvent(
        String email,
        String otp
) {
}
