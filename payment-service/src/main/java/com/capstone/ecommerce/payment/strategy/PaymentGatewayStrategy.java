package com.capstone.ecommerce.payment.strategy;

import com.capstone.ecommerce.payment.dto.Item;
import com.capstone.ecommerce.payment.dto.PaymentLinkInfo;
import com.capstone.ecommerce.payment.exceptions.PaymentLinkGenerationException;
import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentGatewayStrategy {

    String PAYMENT_GATEWAY_STRIPE = "stripe";
    String PAYMENT_GATEWAY_RAZORPAY = "razorpay";

    PaymentLinkInfo createPaymentLink(String orderId, BigDecimal amount, String currency) throws PaymentLinkGenerationException;

    void refundPayment(String paymentId);

    void partialRefund(String paymentId, double amount);

    void handleWebhook(String payload, String signature);
}
