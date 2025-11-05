package com.capstone.ecommerce.payment.strategy;

import com.capstone.ecommerce.payment.dto.Item;
import com.capstone.ecommerce.payment.dto.PaymentLinkInfo;
import com.capstone.ecommerce.payment.exceptions.PaymentLinkGenerationException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StripePaymentGatewayTest {

    @InjectMocks
    private StripePaymentGateway stripePaymentGateway;

    private static final String TEST_WEBHOOK_SECRET = "whsec_test_secret";
    private static final String TEST_REDIRECT_URL = "http://localhost:3000/payment/status";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(stripePaymentGateway, "WEBHOOK_SECRET", TEST_WEBHOOK_SECRET);
        ReflectionTestUtils.setField(stripePaymentGateway, "REDIRECT_URL", TEST_REDIRECT_URL);
    }

    @Test
    void createPaymentLink_Success() {
        // Arrange
        String orderId = "order123";
        List<Item> items = List.of(
            new Item("Product 1", 2, 100.0),
            new Item("Product 2", 1, 50.0)
        );
        String currency = "usd";

        Session mockSession = mock(Session.class);
        when(mockSession.getUrl()).thenReturn("https://stripe.com/pay/test");
        when(mockSession.getExpiresAt()).thenReturn(System.currentTimeMillis() / 1000L);

        try (MockedStatic<Session> mockedStatic = Mockito.mockStatic(Session.class)) {
            mockedStatic.when(() -> Session.create(any(SessionCreateParams.class))).thenReturn(mockSession);

            // Act
            PaymentLinkInfo result = stripePaymentGateway.createPaymentLink(orderId, items, currency);

            // Assert
            assertNotNull(result);
            assertEquals("https://stripe.com/pay/test", result.link());
            assertEquals(TEST_REDIRECT_URL, result.redirectUrl());
            assertTrue(result.expiresAt() > 0);
        }
    }

    @Test
    void createPaymentLink_ThrowsException() {
        // Arrange
        String orderId = "order123";
        List<Item> items = List.of(new Item("Product 1", 1, 100.0));
        String currency = "usd";

        try (MockedStatic<Session> mockedStatic = Mockito.mockStatic(Session.class)) {
            mockedStatic.when(() -> Session.create(any(SessionCreateParams.class)))
                    .thenThrow(new InvalidRequestException("Test error", "param", "invalid_request", null, null, null));

            // Act & Assert
            assertThrows(PaymentLinkGenerationException.class, () ->
                stripePaymentGateway.createPaymentLink(orderId, items, currency)
            );
        }
    }

    @Test
    void handleWebhook_Success() {
        // Arrange
        String payload = "{\"type\":\"checkout.session.completed\"}";
        String signature = "test_signature";

        Event mockEvent = mock(Event.class);
        var deserializer = mock(EventDataObjectDeserializer.class);
        Session mockSession = mock(Session.class);

        when(mockEvent.getType()).thenReturn("checkout.session.completed");
        when(mockEvent.getDataObjectDeserializer()).thenReturn(deserializer);
        when(deserializer.getObject()).thenReturn(java.util.Optional.of(mockSession));
//        when(mockEvent.getData()).thenReturn(new Event.Data());
        when(mockSession.getId()).thenReturn("sess_123");

        try (MockedStatic<Webhook> mockedStatic = Mockito.mockStatic(Webhook.class)) {
            mockedStatic.when(() -> Webhook.constructEvent(payload, signature, TEST_WEBHOOK_SECRET))
                    .thenReturn(mockEvent);

            // Act & Assert
            assertDoesNotThrow(() -> stripePaymentGateway.handleWebhook(payload, signature));
        }
    }

    @Test
    void handleWebhook_InvalidSignature() {
        // Arrange
        String payload = "{\"type\":\"checkout.session.completed\"}";
        String signature = "invalid_signature";

        try (MockedStatic<Webhook> mockedStatic = Mockito.mockStatic(Webhook.class)) {
            mockedStatic.when(() -> Webhook.constructEvent(payload, signature, TEST_WEBHOOK_SECRET))
                    .thenThrow(new SignatureVerificationException("Invalid signature", signature));

            // Act & Assert
            assertThrows(RuntimeException.class, () ->
                stripePaymentGateway.handleWebhook(payload, signature)
            );
        }
    }
}