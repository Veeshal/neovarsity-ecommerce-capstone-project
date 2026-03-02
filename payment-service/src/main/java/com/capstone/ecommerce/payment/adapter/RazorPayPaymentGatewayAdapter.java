package com.capstone.ecommerce.payment.adapter;

import com.capstone.ecommerce.payment.dto.*;
import com.capstone.ecommerce.payment.exceptions.PaymentLinkGenerationException;
import com.capstone.ecommerce.payment.exceptions.RefundException;
import com.capstone.ecommerce.payment.service.RazorPayWebhookService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Getter
@RequiredArgsConstructor
@Component(PaymentGatewayAdapter.PAYMENT_GATEWAY_RAZORPAY)
public class RazorPayPaymentGatewayAdapter implements PaymentGatewayAdapter {

    @Value("${ecom.razorpay.key}")
    private String RAZORPAY_KEY;

    @Value("${ecom.razorpay.secret_key}")
    private String RAZORPAY_SECRET;

    @Value("${ecom.payment_redirect_url}")
    private String REDIRECT_URL;

    @Value("${ecom.razorpay.webhook_secret}")
    private String RAZORPAY_WEBHOOK_SECRET;

    @Value("${ecom.kafka.topics.payment}")
    private String PAYMENT_TOPIC;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final RazorPayWebhookService webhookService;


    @Override
    public PaymentLinkInfo createPaymentLink(String orderId, BigDecimal amount, String currency) throws PaymentLinkGenerationException {
        try {

            RazorpayClient razorpay = new RazorpayClient(RAZORPAY_KEY, RAZORPAY_SECRET);

            int totalAmount = amount.multiply(BigDecimal.valueOf(100)).intValue();

            var request = new RazorPayPaymentLinkRequest.Builder(totalAmount) // amount in paise
                    .currency(currency)
                    .acceptPartial(false)
                    .expireBy(System.currentTimeMillis() / 1000 + 3600) // expires in 1 hour
                    .referenceId("TXN_" + System.currentTimeMillis())
                    .description("Payment for your order " + orderId)
                    .notes(Map.of(
                            "orderId", orderId
                    ))
                    .notification(new RazorPayPaymentLinkRequest.Notification(true, true))
                    .reminderEnable(true)
                    .callbackUrl(REDIRECT_URL)
                    .callbackMethod("get")
                    .build();

            var mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

            var json = mapper.writeValueAsString(request);
            var paymentLinkRequest = new JSONObject(json);
            log.info("Creating Razorpay payment link with request: {}", paymentLinkRequest);

            PaymentLink paymentLink = razorpay.paymentLink.create(paymentLinkRequest);

            log.info(paymentLink.toJson().toString());
            var response = mapper.readValue(paymentLink.toJson().toString(), RazorPayPaymentLinkResponse.class);
            log.info("Parsed Razorpay response: {}", response);
            return new PaymentLinkInfo(
                    response.shortUrl(),
                    response.expireBy(),
                    REDIRECT_URL,
                    response.id(),
                    orderId
            );
        } catch (Exception e) {
            throw new PaymentLinkGenerationException(e);
        }
    }

    @Override
    public void refundPayment(String orderId, String paymentId, String reason) {
        try {
            RazorpayClient razorpay = new RazorpayClient(RAZORPAY_KEY, RAZORPAY_SECRET);

            var refundRequest = new JSONObject()
                    .put("notes", new JSONObject()
                            .put("reason", reason)
                            .put("orderId", orderId));

            var refund = razorpay.payments.refund(paymentId, refundRequest);

            String refundId = refund.get("id");
            String refundStatus = refund.get("status");
            kafkaTemplate.send(PAYMENT_TOPIC, new PaymentEvent(orderId,
                    paymentId, refundId, PaymentEventStatus.REFUND_INITIATED)
            );

            log.info("Refund successful for payment ID: {}, Refund ID: {}", paymentId, refund.get("id"));
        } catch (Exception e) {
            log.error("Error initializing Razorpay client for refund: {}", e.getMessage(), e);
            throw new RefundException(e.getMessage());
        }
    }

    @Override
    public void partialRefund(String orderId, String paymentId, BigDecimal amount, String reason) {
        try {

            RazorpayClient razorpay = new RazorpayClient(RAZORPAY_KEY, RAZORPAY_SECRET);
            int partialAmount = amount.multiply(BigDecimal.valueOf(100)).intValue();

            JSONObject refundRequest = new JSONObject()
                    .put("amount", Integer.toString(partialAmount)) // amount in paise
                    .put("notes", new JSONObject()
                            .put("reason", reason)
                            .put("orderId", orderId));

            var refund = razorpay.payments.refund(paymentId, refundRequest);

            String refundId = refund.get("id");
            String refundStatus = refund.get("status");

            log.info("Partial refund successful for payment ID: {}, Refund ID: {}, Amount: {}", paymentId, refund.get("id"), amount);
            kafkaTemplate.send(PAYMENT_TOPIC, new PaymentEvent(orderId,
                    paymentId, refundId, PaymentEventStatus.PARTIAL_REFUND_INITIATED)
            );
        } catch (Exception e) {
            log.error("Error initializing Razorpay client for partial refund: {}", e.getMessage(), e);
            throw new RefundException(e.getMessage());
        }
    }

    @Override
    public void handleWebhook(String payload, String signature) {

        try {
            // Step 1: Verify the webhook signature
            boolean isValid = Utils.verifyWebhookSignature(payload, signature, RAZORPAY_WEBHOOK_SECRET);
            if (!isValid) {
                log.info("Invalid webhook signature");
                return;
            }

            // Step 2: Parse the payload
            JSONObject event = new JSONObject(payload);
            String eventType = event.getString("event");

            log.info("Webhook event received: {}", eventType);

            // Step 3: Extract payment details based on event type
            JSONObject payloadObj = event.getJSONObject("payload");

            if (webhookService.isPaymentEvent(eventType)) {
                webhookService.processPayment(payloadObj);
            } else if (webhookService.isRefundEvent(eventType)) {
                webhookService.processRefund(payloadObj);
            } else if (webhookService.isPaymentLinkEvent(eventType)) {
                webhookService.processPaymentLink(payloadObj);
            } else {
                log.info("Unhandled event type: {}", eventType);
            }

        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }
}

