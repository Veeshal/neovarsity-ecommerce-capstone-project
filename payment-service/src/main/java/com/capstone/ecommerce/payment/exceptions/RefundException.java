package com.capstone.ecommerce.payment.exceptions;

public class RefundException extends RuntimeException {
    public RefundException(String message) {
        super(message);
    }
}
