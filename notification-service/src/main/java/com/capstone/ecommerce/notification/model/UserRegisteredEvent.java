package com.capstone.ecommerce.notification.model;

public record UserRegisteredEvent(
        long userId,
        String username,
        String email,
        String registrationDate,
        String firstName,
        String lastName
) {
}
