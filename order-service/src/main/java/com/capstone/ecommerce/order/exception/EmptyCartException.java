package com.capstone.ecommerce.order.exception;

public class EmptyCartException extends RuntimeException {
    public EmptyCartException() {
        super("Cart is empty. Cannot place order.");
    }
}
