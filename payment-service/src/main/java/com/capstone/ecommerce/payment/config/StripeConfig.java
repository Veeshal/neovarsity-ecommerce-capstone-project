package com.capstone.ecommerce.payment.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {

    @Value("${ecom.stripe.secret_key}")
    private String STRIPE_SECRET_KEY;

    @Value("${ecom.stripe.publishable_key}")
    private String STRIPE_PUBLISHABLE_KEY;

    @PostConstruct
    public void init() {
        Stripe.apiKey = STRIPE_SECRET_KEY;
    }

}
