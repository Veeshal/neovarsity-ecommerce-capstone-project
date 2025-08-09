package com.capstone.ecommerce.cart.exceptions;

public class OrderCreationFailedException extends RuntimeException {
    public OrderCreationFailedException(String message) {
        super(message);
    }
}
