package com.capstone.ecommerce.payment.exceptions;

public class PaymentLinkGenerationException extends RuntimeException {

    public PaymentLinkGenerationException(Exception e) {
        super("Failed to generate payment link: " + e.getMessage(), e);
    }
}
