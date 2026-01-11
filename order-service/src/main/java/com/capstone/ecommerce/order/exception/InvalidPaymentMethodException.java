package com.capstone.ecommerce.order.exception;

public class InvalidPaymentMethodException extends RuntimeException {
    public InvalidPaymentMethodException(String code) {
        super("Invalid payment method: " + code);
    }
}
