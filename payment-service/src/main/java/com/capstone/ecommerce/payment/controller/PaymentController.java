package com.capstone.ecommerce.payment.controller;

import com.capstone.ecommerce.payment.service.PaymentService;
import com.capstone.ecommerce.payment.strategy.PaymentGatewayStrategy;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.billingportal.Session;
import com.stripe.net.Webhook;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public String createPaymentLink(String paymentGateway, String paymentDetails) {
        log.info("Creating payment link for gateway: {}, details: {}", paymentGateway, paymentDetails);
        return paymentService.createPaymentLink(paymentGateway, paymentDetails);
    }

    @PostMapping("/webhook/stripe")
    public void handleStripeWebhook(HttpServletRequest request) {
//        log.info("Received Stripe webhook: {}", payload);
        paymentService.handleWebhook(PaymentGatewayStrategy.PAYMENT_GATEWAY_STRIPE, request);
    }





        @PostMapping("/webhook/razorpay")
    public void handleStripeRazorpay(HttpServletRequest request) {
//        log.info("Received Razorpay webhook: {}", payload);
        paymentService.handleWebhook(PaymentGatewayStrategy.PAYMENT_GATEWAY_RAZORPAY, request);
    }

}
