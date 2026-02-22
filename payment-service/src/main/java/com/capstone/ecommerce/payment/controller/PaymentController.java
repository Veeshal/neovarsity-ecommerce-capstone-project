package com.capstone.ecommerce.payment.controller;

import com.capstone.ecommerce.payment.dto.PaymentLinkGenerateRequest;
import com.capstone.ecommerce.payment.dto.PaymentLinkGenerateResponse;
import com.capstone.ecommerce.payment.service.PaymentService;
import com.capstone.ecommerce.payment.strategy.PaymentGatewayStrategy;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@AllArgsConstructor
@Validated
@RestController
@RequestMapping("v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public PaymentLinkGenerateResponse createPaymentLink(@RequestBody PaymentLinkGenerateRequest request) {
        log.info("Creating payment link for request: {}", request);
        var paymentLinkInfo = paymentService.createPaymentLink(
                request.gateway(),
                request.orderId(),
                request.items(),
                request.currency());

        return new PaymentLinkGenerateResponse(
                paymentLinkInfo.link(),
                paymentLinkInfo.expiresAt(),
                paymentLinkInfo.redirectUrl(),
                paymentLinkInfo.paymentId(),
                paymentLinkInfo.orderId());
    }

    @PostMapping("/webhook/stripe")
    public void handleStripeWebhook(@RequestBody String payload,
                                    @RequestHeader("Stripe-Signature") String signature) {
        log.info("Received Stripe webhook: {}", payload);
        paymentService.handleWebhook(PaymentGatewayStrategy.PAYMENT_GATEWAY_STRIPE, payload, signature);
    }





    @PostMapping("/webhook/razorpay")
    public void handleStripeRazorpay(@RequestBody String payload,
                                     @RequestHeader("X-Razorpay-Signature") String signature) {
        log.info("Received Razorpay webhook: {}", payload);
        paymentService.handleWebhook(PaymentGatewayStrategy.PAYMENT_GATEWAY_RAZORPAY, payload, signature);
    }

}
