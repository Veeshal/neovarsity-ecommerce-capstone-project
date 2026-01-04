package com.capstone.ecommerce.cart.exceptions;

public class InvalidPaymentMethodException extends RuntimeException {

    public static InvalidPaymentMethodException forPaymentMethodId(int paymentMethodId) {
        return new InvalidPaymentMethodException(paymentMethodId);
    }

    private InvalidPaymentMethodException(int paymentMethodId) {
        super("Invalid payment method ID: " + paymentMethodId);
    }
}
