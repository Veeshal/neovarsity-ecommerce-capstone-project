package com.capstone.ecommerce.cart.exceptions;

public class EmptyCartException extends RuntimeException {

    public static EmptyCartException forUserId(Long userId) {
        return new EmptyCartException(userId);
    }

    private EmptyCartException(Long userId) {
        super("Cart is empty. Cannot create order from an empty cart for user ID: " + userId);
    }
}
