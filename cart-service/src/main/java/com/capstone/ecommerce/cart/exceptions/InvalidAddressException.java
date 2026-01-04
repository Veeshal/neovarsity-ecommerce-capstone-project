package com.capstone.ecommerce.cart.exceptions;

public class InvalidAddressException extends RuntimeException {

    public static InvalidAddressException forAddressId(Long addressId, Long userId) {
        return new InvalidAddressException(addressId, userId);
    }

    private InvalidAddressException(Long addressId, Long userId) {
        super("Invalid address ID: " + addressId + " for user ID: " + userId);
    }
}
