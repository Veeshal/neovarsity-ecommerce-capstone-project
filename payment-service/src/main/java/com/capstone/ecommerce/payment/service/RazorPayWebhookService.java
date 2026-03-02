package com.capstone.ecommerce.payment.service;

import com.capstone.ecommerce.payment.dto.PaymentEvent;
import com.capstone.ecommerce.payment.dto.PaymentEventStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class RazorPayWebhookService {

    @Value("${ecom.kafka.topics.payment}")
    private String PAYMENT_TOPIC;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public boolean isRefundEvent(String eventType) {
        return List.of("refund.created", "refund.processed", "refund.failed").contains(eventType);
    }

    public boolean isPaymentLinkEvent(String eventType) {
        return List.of("payment_link.paid", "payment_link.cancelled", "payment_link.expired").contains(eventType);
    }

    public boolean isPaymentEvent(String eventType) {
        return List.of("payment.captured", "payment.failed").contains(eventType);
    }


    public void processPaymentLink(JSONObject payloadObj) {
        // Handle payment link events (if using payment link events)
        JSONObject paymentLinkObj = payloadObj.getJSONObject("payment_link").getJSONObject("entity");
        String paymentLinkId = paymentLinkObj.getString("id");
        String status = paymentLinkObj.getString("status");

        String orderId = paymentLinkObj.getJSONObject("notes").getString("orderId");

        log.info("PaymentLink ID: {}, Status: {}", paymentLinkId, status);

        if ("paid".equalsIgnoreCase(status)) {
            kafkaTemplate.send(PAYMENT_TOPIC, new PaymentEvent(orderId, null, paymentLinkId, PaymentEventStatus.PAYMENT_LINK_PAID));
        } else if ("cancelled".equalsIgnoreCase(status)) {
            kafkaTemplate.send(PAYMENT_TOPIC, new PaymentEvent(orderId, null, paymentLinkId, PaymentEventStatus.PAYMENT_LINK_CANCELLED));
        } else if ("expired".equalsIgnoreCase(status)) {
            kafkaTemplate.send(PAYMENT_TOPIC, new PaymentEvent(orderId, null, paymentLinkId, PaymentEventStatus.PAYMENT_LINK_EXPIRED));
        }
    }

    public void processRefund(JSONObject payloadObj) {
        // Handle refund events (if using refund events)
        JSONObject refundObj = payloadObj.getJSONObject("refund").getJSONObject("entity");
        String paymentId = refundObj.getString("payment_id");
        String refundId = refundObj.getString("id");
        String orderId = refundObj.getJSONObject("notes").getString("orderId");
        String refundStatus = refundObj.getString("status");

        log.info("Refund event received for payment ID: {}, Refund ID: {}, Status: {}", paymentId, refundId, refundStatus);

        if ("processed".equalsIgnoreCase(refundStatus)) {
            processRefundEvent(orderId, paymentId, refundId, PaymentEventStatus.REFUND_COMPLETED);
        } else if ("failed".equalsIgnoreCase(refundStatus)) {
            processRefundEvent(orderId, paymentId, refundId, PaymentEventStatus.REFUND_FAILED);
        }
    }

    public void processPayment(JSONObject payloadObj) {
        // Handle direct payment object (if using payment events)
        JSONObject paymentObj = payloadObj.getJSONObject("payment").getJSONObject("entity");
        String paymentId = paymentObj.getString("id");
        String paymentStatus = paymentObj.getString("status");
        String orderId = paymentObj.getJSONObject("notes").getString("orderId");

        log.info("Payment ID: {}, Status: {}", paymentId, paymentStatus);

        if ("captured".equalsIgnoreCase(paymentStatus)) {
            processPaymentCapturedEvent(orderId, paymentId);
        } else if ("failed".equalsIgnoreCase(paymentStatus)) {
            processPaymentFailedEvent(orderId, paymentId);
        }
    }


    public void processRefundEvent(String orderId, String paymentId, String refundId, PaymentEventStatus status) {
        log.info("Processing refund event for orderId: {}, paymentId: {}, refundId: {}, status: {}", orderId, paymentId, refundId, status);
        kafkaTemplate.send(PAYMENT_TOPIC, new PaymentEvent(orderId, paymentId, refundId, status));
    }

    public void processPaymentCapturedEvent(String orderId, String paymentId) {
        log.info("Processing payment captured event for orderId: {}, paymentId: {}", orderId, paymentId);
        kafkaTemplate.send(PAYMENT_TOPIC, new PaymentEvent(orderId, paymentId, null, PaymentEventStatus.PAYMENT_COMPLETED));
    }

    public void processPaymentFailedEvent(String orderId, String paymentId) {
        log.info("Processing payment failed event for orderId: {}, paymentId: {}", orderId, paymentId);
        kafkaTemplate.send(PAYMENT_TOPIC, new PaymentEvent(orderId, paymentId, null, PaymentEventStatus.PAYMENT_FAILED));
    }
}
