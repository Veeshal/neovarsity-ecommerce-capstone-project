package com.capstone.ecommerce.payment.strategy;

import com.stripe.Stripe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StripePaymentGatewayTest {

    private StripePaymentGateway stripePaymentGateway;

    @BeforeEach
    void setUp() {
        // Initialize the StripePaymentGateway with necessary configurations
        // This could include setting the STRIPE_API_KEY or any other required setup
        stripePaymentGateway = new StripePaymentGateway();
    }

    @Test
    void createPaymentLink() {
        var link = stripePaymentGateway.createPaymentLink("Test Payment Details");
        System.out.println(link);
        assertNotNull(link, "Payment link should not be null");
    }

    @Test
    void getPrice() {
    }
}