package com.capstone.ecommerce.payment.strategy;

import com.capstone.ecommerce.payment.dto.Item;
import com.capstone.ecommerce.payment.dto.PaymentEvent;
import com.capstone.ecommerce.payment.dto.PaymentEventStatus;
import com.capstone.ecommerce.payment.dto.PaymentLinkInfo;
import com.capstone.ecommerce.payment.exceptions.PaymentLinkGenerationException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;

import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component(PaymentGatewayStrategy.PAYMENT_GATEWAY_STRIPE)
public class StripePaymentGateway implements PaymentGatewayStrategy {

    @Value("${ecom.stripe.webhook_secret}")
    private String WEBHOOK_SECRET;

    @Value("${ecom.payment_redirect_url}")
    private String REDIRECT_URL;

    @Value("${ecom.kafka.topics.payment}")
    private String PAYMENT_TOPIC;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PaymentLinkInfo createPaymentLink(String orderId, List<Item> items, String currency)
            throws PaymentLinkGenerationException {

        log.debug("Processing payment with Stripe...");

        try {

            var lineItems = items.stream().map(s -> SessionCreateParams.LineItem.builder()
                    .setQuantity((long) s.quantity())
                    .setPriceData(
                            SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency(currency)
                                    .setUnitAmount((long) (s.price() * 100)) // Convert to cents
                                    .setProductData(
                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                    .setName(s.itemName())
                                                    .build())
                                    .build())
                    .build()).toList();

            var sessionCreateParams = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .putMetadata("orderId", orderId)
                    .setSuccessUrl(REDIRECT_URL)
                    .setCancelUrl(REDIRECT_URL + "?canceled=true")
                    .addAllLineItem(lineItems)
                    .build();


            var session = Session.create(sessionCreateParams);
            var paymentIntentId = session.getPaymentIntent();

            return new PaymentLinkInfo(
                    session.getUrl(),
                    session.getExpiresAt(),
                    REDIRECT_URL,
                    paymentIntentId,
                    orderId
            );
        } catch (StripeException e) {
            throw new PaymentLinkGenerationException(e);
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
                        var paymentEvent = new PaymentEvent(orderId, paymentId, PaymentEventStatus.COMPLETED);
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
                        var paymentEvent = new PaymentEvent(orderId, paymentId, PaymentEventStatus.FAILED);
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
