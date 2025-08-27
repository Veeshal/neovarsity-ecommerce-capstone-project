package com.capstone.ecommerce.user.dto;

import com.capstone.ecommerce.user.entity.UserAddress;

public record AddressDto(
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
    public static AddressDto from(UserAddress address) {
        return new AddressDto(
                address.getId(),
                address.getAddressName(),
                address.getAddressLine1(),
                address.getAddressLine2(),
                address.getCity(),
                address.getState(),
                address.getPostalCode(),
                address.getCountry(),
                address.getPhoneNumber(),
                address.getLatitude(),
                address.getLongitude(),
                address.isDefault()
        );
    }
}
