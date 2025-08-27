package com.capstone.ecommerce.user.exceptions;

public class InvalidUserAddressException extends RuntimeException {
    public InvalidUserAddressException(Long addressId) {
        super("Address not found for addressId " + addressId);
    }
}
