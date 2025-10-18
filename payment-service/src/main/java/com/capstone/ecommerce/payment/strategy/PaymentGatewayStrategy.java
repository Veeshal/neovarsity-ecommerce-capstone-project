package com.capstone.ecommerce.payment.strategy;

import com.capstone.ecommerce.payment.exceptions.PaymentLinkGenerationException;
import jakarta.servlet.http.HttpServletRequest;

public interface PaymentGatewayStrategy {

    String PAYMENT_GATEWAY_STRIPE = "stripe";
    String PAYMENT_GATEWAY_RAZORPAY = "razorpay";

    String createPaymentLink(String paymentDetails) throws PaymentLinkGenerationException;

    void handleWebhook(HttpServletRequest request);
}
