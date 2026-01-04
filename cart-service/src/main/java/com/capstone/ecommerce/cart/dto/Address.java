package com.capstone.ecommerce.cart.dto;

public record Address(
        Long id,
        String addressName,
        String addressLine1,
        String addressLine2,
        String city,
        String state,
        String postalCode,
        String country,
        String phoneNumber,
        Double latitude,
        Double longitude,
        Boolean isDefault
) {
}
