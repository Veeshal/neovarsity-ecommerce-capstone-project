package com.capstone.ecommerce.payment.adapter;

import com.capstone.ecommerce.payment.dto.PaymentLinkInfo;
import com.capstone.ecommerce.payment.exceptions.PaymentLinkGenerationException;

import java.math.BigDecimal;

public interface PaymentGatewayAdapter {

    String PAYMENT_GATEWAY_STRIPE = "stripe";
    String PAYMENT_GATEWAY_RAZORPAY = "razorpay";

    PaymentLinkInfo createPaymentLink(String orderId, BigDecimal amount, String currency) throws PaymentLinkGenerationException;

    void refundPayment(String orderId, String paymentId, String reason);

    void partialRefund(String orderId, String paymentId, BigDecimal amount, String reason);

    void handleWebhook(String payload, String signature);
}

