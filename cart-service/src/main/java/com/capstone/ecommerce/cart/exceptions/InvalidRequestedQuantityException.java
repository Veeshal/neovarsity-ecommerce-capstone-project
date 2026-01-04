package com.capstone.ecommerce.cart.exceptions;

public class InvalidRequestedQuantityException extends RuntimeException {

    public static InvalidRequestedQuantityException getInstance(int quantity, Long productId) {
        return new InvalidRequestedQuantityException(quantity, productId);
    }

    private InvalidRequestedQuantityException(int quantity, Long productId) {
        super("Invalid requested quantity: " + quantity + " for product ID: " + productId);
    }
}
