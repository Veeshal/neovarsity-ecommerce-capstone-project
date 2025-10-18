package com.capstone.ecommerce.payment.strategy;

import com.capstone.ecommerce.payment.exceptions.PaymentLinkGenerationException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentLink;
import com.stripe.model.Price;
import com.stripe.model.billingportal.Session;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentLinkCreateParams;
import com.stripe.param.PriceCreateParams;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.stream.Collectors;

@Slf4j
@Component(PaymentGatewayStrategy.PAYMENT_GATEWAY_STRIPE)
public class StripePaymentGateway implements PaymentGatewayStrategy {

    @Value("${ecom.stripe.webhook_secret}")
    private String WEBHOOK_SECRET;

    @Value("${ecom.payment_redirect_url}")
    private String REDIRECT_URL;

    public String createPaymentLink(String paymentDetails) throws PaymentLinkGenerationException {
        // Logic to process payment using Stripe API
        // This is a placeholder for actual Stripe integration code

        log.debug("Processing payment with Stripe: {}", paymentDetails);

        try {
            Price price = getPrice("50"); // TODO: Update price from request.

            var params = PaymentLinkCreateParams.builder().addLineItem(
                    PaymentLinkCreateParams.LineItem.builder()
                            .setPrice(price.getId())
                            .setQuantity(1L)
                            .build()
                    )
                    .setAfterCompletion(
                            PaymentLinkCreateParams.AfterCompletion.builder()
                                    .setType(PaymentLinkCreateParams.AfterCompletion.Type.REDIRECT)
                                    .setRedirect(
                                            PaymentLinkCreateParams.AfterCompletion.Redirect.builder()
                                                    .setUrl(REDIRECT_URL)
                                                    .build())
                                    .build())
                    .build();

            PaymentLink paymentLink = PaymentLink.create(params);
            return paymentLink.getUrl();
        } catch (Exception e) {
            throw new PaymentLinkGenerationException(e);
        }
    }

    public Price getPrice(String priceId) {
        // Logic to retrieve price details from Stripe
        // This is a placeholder for actual Stripe integration code
        log.info("Retrieving price details for: {}", priceId);

        try {
            PriceCreateParams params =
                    PriceCreateParams.builder()
                            .setCurrency("usd")
                            .setUnitAmount(50000L)
                            .setProductData(
                                    PriceCreateParams.ProductData.builder()
                                            .setName("Product Name")
                                            .build()
                            )
                            .build();
            Price price = Price.create(params);
            return price;
        } catch (Exception e) {
            throw new PaymentLinkGenerationException(e);
        }
    }



//    public String inlinePricing() {
//        SessionCreateParams sessionCreateParams = SessionCreateParams.builder().build();
//    }

    @Override
    public void handleWebhook(HttpServletRequest request) {

        try {

            String payload = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            String sigHeader = request.getHeader("Stripe-Signature");

            log.info("Received Stripe webhook with signature: {}", sigHeader);
            log.info("Received Stripe webhook with payload: {}", payload);

            Event event = Webhook.constructEvent(
                    payload,
                    sigHeader,
                    WEBHOOK_SECRET // your webhook secret from Stripe Dashboard
            );

            switch (event.getType()) {
                case "payment_intent.succeeded":
                    PaymentIntent intent = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElse(null);
                    if (intent != null) {
                        log.info("Payment succeeded for: {}", intent.getId());
                        // TODO: fulfill order here
                    }
                    break;

                case "checkout.session.completed":
                    Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
                    if (session != null) {
                        log.info("Checkout completed: {}", session.getId());
                        // e.g., use session.getCustomerDetails()
                    }
                    break;

                default:
                    log.info("Unhandled event type: {}", event.getType());
            }

        } catch (SignatureVerificationException e) {
            // Invalid signature
            log.error("Webhook error while validating signature.", e);
            throw new RuntimeException("Webhook error: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException("IO error: " + e.getMessage());
        }
    }
}
