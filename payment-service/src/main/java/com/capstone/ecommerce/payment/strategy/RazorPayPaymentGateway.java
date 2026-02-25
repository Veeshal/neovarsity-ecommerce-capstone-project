package com.capstone.ecommerce.payment.strategy;

import com.capstone.ecommerce.payment.dto.*;
import com.capstone.ecommerce.payment.exceptions.PaymentLinkGenerationException;
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
@Component(PaymentGatewayStrategy.PAYMENT_GATEWAY_RAZORPAY)
public class RazorPayPaymentGateway implements PaymentGatewayStrategy {

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


    @Override
    public PaymentLinkInfo createPaymentLink(String orderId, BigDecimal amount, String currency) throws PaymentLinkGenerationException {
        try {

            RazorpayClient razorpay = new RazorpayClient(RAZORPAY_KEY, RAZORPAY_SECRET);

            var request = new RazorPayPaymentLinkRequest.Builder(amount.multiply(BigDecimal.valueOf(100)).intValue()) // amount in paise
                    .currency(currency)
                    .acceptPartial(false)
                    .expireBy(System.currentTimeMillis() / 1000 + 3600) // expires in 1 hour
                    .referenceId("TXN_" + System.currentTimeMillis())
                    .description("Payment for your order")
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

//            http://localhost:8080/payment/status?razorpay_payment_id=pay_SDJcUp47uQnR58&razorpay_payment_link_id=plink_SCm6Jgpsj2m5Ed&razorpay_payment_link_reference_id=TXN_1770362298666&razorpay_payment_link_status=paid&razorpay_signature=9eb8ff3262fbe23637266f253fcb0a5099eb41291dd03a55ea771412a7e49d22

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
    public void refundPayment(String paymentId) {
        try {
            RazorpayClient razorpay = new RazorpayClient(RAZORPAY_KEY, RAZORPAY_SECRET);

            var refundRequest = new JSONObject()
                    .put("notes", new JSONObject()
                            .put("reason", "Customer requested full refund"));

            var refund = razorpay.payments.refund(paymentId, refundRequest);

            log.info("Refund successful for payment ID: {}, Refund ID: {}", paymentId, refund.get("id"));
            log.info("Refund details: {}", refund.toJson().toString());
            // TODO: Notify customer about refund status via email/SMS using notification service
        } catch (Exception e) {
            log.error("Error initializing Razorpay client for refund: {}", e.getMessage(), e);
        }
    }

    @Override
    public void partialRefund(String paymentId, double amount) {
        try {

            RazorpayClient razorpay = new RazorpayClient(RAZORPAY_KEY, RAZORPAY_SECRET);

            JSONObject refundRequest = new JSONObject()
                    .put("amount", (int) (amount * 100)) // amount in paise
                    .put("notes", new JSONObject()
                            .put("reason", "Customer requested partial refund"));

            var refund = razorpay.payments.refund(paymentId, refundRequest);

            log.info("Partial refund successful for payment ID: {}, Refund ID: {}, Amount: {}", paymentId, refund.get("id"), amount);
            log.info("Refund details: {}", refund.toJson().toString());
            // TODO: Notify customer about partial refund status via email/SMS using notification service
        } catch (Exception e) {
            log.error("Error initializing Razorpay client for partial refund: {}", e.getMessage(), e);
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
            if (payloadObj.has("payment_link")) {
                JSONObject paymentLinkObj = payloadObj.getJSONObject("payment_link").getJSONObject("entity");
                String paymentLinkId = paymentLinkObj.getString("id");
                String status = paymentLinkObj.getString("status");

                log.info("PaymentLink ID: {}", paymentLinkId);
                log.info("Status: {}", status);

                // Step 4: Handle business logic
                if ("paid".equalsIgnoreCase(status)) {
                    // Update order/payment record in your database
                    log.info("Payment successful for link: {}", paymentLinkId);
                } else if ("cancelled".equalsIgnoreCase(status)) {
                    log.info("Payment link was cancelled.");
                }
            } else if (payloadObj.has("payment")) {
                // Handle direct payment object (if using payment events)
                JSONObject paymentObj = payloadObj.getJSONObject("payment").getJSONObject("entity");
                String paymentId = paymentObj.getString("id");
                String paymentStatus = paymentObj.getString("status");
                String orderId = paymentObj.getJSONObject("notes").getString("orderId");

                log.info("Payment ID: {}, Status: {}", paymentId, paymentStatus);
                if ("captured".equalsIgnoreCase(paymentStatus)) {
                    log.info("Payment captured successfully for payment ID: {}", paymentId);

                    var paymentEvent = new PaymentEvent(orderId, paymentId, PaymentEventStatus.COMPLETED);
                    kafkaTemplate.send(PAYMENT_TOPIC, paymentEvent);

                } else if ("failed".equalsIgnoreCase(paymentStatus)) {
                    log.info("Payment failed for payment ID: {}", paymentId);

                    var paymentEvent = new PaymentEvent(orderId, paymentId, PaymentEventStatus.FAILED);
                    kafkaTemplate.send(PAYMENT_TOPIC, paymentEvent);
                }
            }

        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }
}
