package com.capstone.ecommerce.user.stream.event;

public record PasswordResetEvent(
        Long userId,
        String email,
        String phone,
        String token
) {
}
