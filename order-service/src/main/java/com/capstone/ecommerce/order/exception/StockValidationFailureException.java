package com.capstone.ecommerce.order.exception;

public class StockValidationFailureException extends RuntimeException {
    public StockValidationFailureException(String message) {
        super(message);
    }
}
