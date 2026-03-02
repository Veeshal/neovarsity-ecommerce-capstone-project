package com.capstone.ecommerce.payment.adapter;

import com.capstone.ecommerce.payment.dto.PaymentEvent;
import com.capstone.ecommerce.payment.dto.PaymentEventStatus;
import com.capstone.ecommerce.payment.dto.PaymentLinkInfo;
import com.capstone.ecommerce.payment.exceptions.PaymentLinkGenerationException;
import com.capstone.ecommerce.payment.exceptions.RefundException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;

import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.RefundCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@RequiredArgsConstructor
@Component(PaymentGatewayAdapter.PAYMENT_GATEWAY_STRIPE)
public class StripePaymentGatewayAdapter implements PaymentGatewayAdapter {

    @Value("${ecom.stripe.webhook_secret}")
    private String WEBHOOK_SECRET;

    @Value("${ecom.payment_redirect_url}")
    private String REDIRECT_URL;

    @Value("${ecom.kafka.topics.payment}")
    private String PAYMENT_TOPIC;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PaymentLinkInfo createPaymentLink(String orderId, BigDecimal amount, String currency)
            throws PaymentLinkGenerationException {

        log.debug("Processing payment with Stripe...");

        try {
            var lineItem = SessionCreateParams.LineItem.builder()
                    .setQuantity(1L)
                    .setPriceData(
                            SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency(currency)
                                    .setUnitAmount((long) (amount.doubleValue() * 100)) // Convert to cents
                                    .setProductData(
                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                    .setName("Order " + orderId)
                                                    .build())
                                    .build())
                    .build();

            var sessionCreateParams = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .putMetadata("orderId", orderId)
                    .setSuccessUrl(REDIRECT_URL)
                    .setCancelUrl(REDIRECT_URL + "?canceled=true")
                    .addLineItem(lineItem)
                    .build();


            var session = Session.create(sessionCreateParams);

            return new PaymentLinkInfo(
                    session.getUrl(),
                    session.getExpiresAt(),
                    REDIRECT_URL,
                    session.getId(),
                    orderId
            );
        } catch (StripeException e) {
            throw new PaymentLinkGenerationException(e);
        }
    }

    @Override
    public void refundPayment(String orderId, String paymentId, String reason) {
        try {
            RefundCreateParams params =
                    RefundCreateParams.builder()
                            .setPaymentIntent(paymentId)
                            .setReason(RefundCreateParams.Reason.REQUESTED_BY_CUSTOMER)
                            .putMetadata("orderId", orderId)
                            .build();
            var refund = Refund.create(params);
            String refundId = refund.getId();
            String refundStatus = refund.getStatus();
            log.info("Refund successful for payment intent {}: {}", paymentId, refund.getId());
            kafkaTemplate.send(PAYMENT_TOPIC, new PaymentEvent(orderId,
                    paymentId, refundId, PaymentEventStatus.REFUND_INITIATED)
            );
        } catch (StripeException e) {
            log.error("Error processing refund for payment intent {}: {}", paymentId, e.getMessage());
            throw new RefundException("Refund failed: " + e.getMessage());
        }
    }

    @Override
    public void partialRefund(String orderId, String paymentId, BigDecimal amount, String reason) {
        try {

            long totalAmount = amount.multiply(BigDecimal.valueOf(100)).longValue(); // Convert to cents
            RefundCreateParams params =
                    RefundCreateParams.builder()
                            .setPaymentIntent(paymentId)
                            .setAmount(totalAmount) // Convert to cents
                            .setReason(RefundCreateParams.Reason.REQUESTED_BY_CUSTOMER)
                            .putMetadata("orderId", orderId)
                            .build();
            var refund = Refund.create(params);
            String refundId = refund.getId();
            String refundStatus = refund.getStatus();
            log.info("Partial refund successful for payment intent {}: {}, Amount: {}", paymentId, refund.getId(), amount);
            kafkaTemplate.send(PAYMENT_TOPIC, new PaymentEvent(orderId,
                    paymentId, refundId, PaymentEventStatus.PARTIAL_REFUND_INITIATED)
            );
        } catch (StripeException e) {
            log.error("Error processing partial refund for payment intent {}: {}", paymentId, e.getMessage());
            throw new RefundException("Partial refund failed: " + e.getMessage());
        }
    }

    @Override
    public void handleWebhook(String payload, String signature) {

        try {

            log.info("Received Stripe webhook with signature: {}", signature);
            log.info("Received Stripe webhook with payload: {}", payload);

            Event event = Webhook.constructEvent(
                    payload,
                    signature,
                    WEBHOOK_SECRET // your webhook secret from Stripe Dashboard
            );

            switch (event.getType()) {

                case "checkout.session.completed":
                    Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
                    if (session != null) {
                        log.info("Checkout completed: {}", session.getId());
                        // e.g., use session.getCustomerDetails()
                        String orderId = session.getMetadata().get("orderId");
                        String paymentId = session.getPaymentIntent();
                        String paymentLinkId = session.getId();
                        var paymentEvent = new PaymentEvent(orderId, paymentId, null, PaymentEventStatus.PAYMENT_LINK_PAID);
                        kafkaTemplate.send(PAYMENT_TOPIC, paymentEvent);
                    }
                    break;
                case "checkout.session.expired":
                    Session expired = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
                    if (expired != null) {
                        log.info("Checkout expired: {}", expired.getId());
                        // e.g., use session.getCustomerDetails()
                        String orderId = expired.getMetadata().get("orderId");
                        String paymentId = expired.getPaymentIntent();
                        var paymentEvent = new PaymentEvent(orderId, paymentId, null, PaymentEventStatus.PAYMENT_LINK_EXPIRED);
                        kafkaTemplate.send(PAYMENT_TOPIC, paymentEvent);
                    }
                    break;
                case "refund.created":
                    Refund refund = (Refund) event.getDataObjectDeserializer().getObject().orElse(null);
                    if (refund != null) {
                        log.info("Refund created: {}", refund.getId());
                        String paymentId = refund.getPaymentIntent();
                        String orderId = refund.getMetadata().get("orderId");
                        String refundId = refund.getId();
                        var paymentEvent = new PaymentEvent(orderId, paymentId, refundId, PaymentEventStatus.REFUND_INITIATED);
                        kafkaTemplate.send(PAYMENT_TOPIC, paymentEvent);
                    }
                    break;
                case "refund.updated":
                    Refund updatedRefund = (Refund) event.getDataObjectDeserializer().getObject().orElse(null);
                    if (updatedRefund != null) {
                        log.info("Refund updated: {}", updatedRefund.getId());
                        String paymentId = updatedRefund.getPaymentIntent();
                        String orderId = updatedRefund.getMetadata().get("orderId");
                        String refundId = updatedRefund.getId();
                        PaymentEventStatus status = "succeeded".equalsIgnoreCase(updatedRefund.getStatus()) ? PaymentEventStatus.REFUND_COMPLETED : PaymentEventStatus.REFUND_FAILED;
                        var paymentEvent = new PaymentEvent(orderId, paymentId, refundId, status);
                        kafkaTemplate.send(PAYMENT_TOPIC, paymentEvent);
                    }
                    break;
                case "refund.failed":
                    Refund failedRefund = (Refund) event.getDataObjectDeserializer().getObject().orElse(null);
                    if (failedRefund != null) {
                        log.info("Refund failed: {}", failedRefund.getId());
                        String paymentId = failedRefund.getPaymentIntent();
                        String orderId = failedRefund.getMetadata().get("orderId");
                        String refundId = failedRefund.getId();
                        var paymentEvent = new PaymentEvent(orderId, paymentId, refundId, PaymentEventStatus.REFUND_FAILED);
                        kafkaTemplate.send(PAYMENT_TOPIC, paymentEvent);
                    }
                    break;
                default:
                    log.info("Unhandled event type: {}", event.getType());
            }

        } catch (SignatureVerificationException e) {
            // Invalid signature
            log.error("Webhook error while validating signature.", e);
            throw new RuntimeException("Webhook error: " + e.getMessage());
        }
    }
}

