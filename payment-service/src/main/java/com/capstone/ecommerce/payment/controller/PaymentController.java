package com.capstone.ecommerce.payment.controller;

import com.capstone.ecommerce.payment.dto.PaymentLinkGenerateRequest;
import com.capstone.ecommerce.payment.dto.PaymentLinkGenerateResponse;
import com.capstone.ecommerce.payment.dto.RefundRequest;
import com.capstone.ecommerce.payment.service.PaymentService;
import com.capstone.ecommerce.payment.adapter.PaymentGatewayAdapter;
import jakarta.validation.Valid;
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
    public PaymentLinkGenerateResponse createPaymentLink(@Valid @RequestBody PaymentLinkGenerateRequest request) {
        log.info("Creating payment link for request: {}", request);
        var paymentLinkInfo = paymentService.createPaymentLink(
                request.gateway(),
                request.orderId(),
                request.amount(),
                request.currency());

        return new PaymentLinkGenerateResponse(
                paymentLinkInfo.link(),
                paymentLinkInfo.expiresAt(),
                paymentLinkInfo.redirectUrl(),
                paymentLinkInfo.paymentLinkId(),
                paymentLinkInfo.orderId());
    }

    @PostMapping("refund")
    public void refundPayment(@Valid @RequestBody RefundRequest request) {
        paymentService.refundPayment(
                request.gateway(),
                request.orderId(),
                request.paymentId(),
                request.reason(),
                request.amount(),
                request.isFullRefund()
        );
    }

    @PostMapping("/webhook/stripe")
    public void handleStripeWebhook(@RequestBody String payload,
                                    @RequestHeader("Stripe-Signature") String signature) {
        log.info("Received Stripe webhook: {}", payload);
        paymentService.handleWebhook(PaymentGatewayAdapter.PAYMENT_GATEWAY_STRIPE, payload, signature);
    }





    @PostMapping("/webhook/razorpay")
    public void handleStripeRazorpay(@RequestBody String payload,
                                     @RequestHeader("X-Razorpay-Signature") String signature) {
        log.info("Received Razorpay webhook: {}", payload);
        paymentService.handleWebhook(PaymentGatewayAdapter.PAYMENT_GATEWAY_RAZORPAY, payload, signature);
    }

}
