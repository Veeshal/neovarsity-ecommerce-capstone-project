package com.capstone.ecommerce.payment.controller;

import com.capstone.ecommerce.payment.dto.PaymentLinkInfo;
import com.capstone.ecommerce.payment.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
class PaymentControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private PaymentService paymentService;

    @Test
    void createPaymentLink() throws Exception {
        given(paymentService.createPaymentLink(any(), any(), any(), any()))
                .willReturn(new PaymentLinkInfo(
                        "http://payment-gateway.com/pay/123",
                        System.currentTimeMillis() + 3600000,
                        "http://myapp.com/payment/redirect/123",
                        "payment123",
                        "order123"
                ));

        mvc.perform(post("/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "gateway": "stripe",
                            "orderId": "order123",
                            "amount": 1000,
                            "currency": "USD"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.link").exists())
                .andExpect(jsonPath("$.link").value("http://payment-gateway.com/pay/123"))
                .andExpect(jsonPath("$.expiresAt").exists())
                .andExpect(jsonPath("$.redirectUrl").exists())
                .andExpect(jsonPath("$.redirectUrl").value("http://myapp.com/payment/redirect/123"))
                .andExpect(jsonPath("$.paymentLinkId").exists())
                .andExpect(jsonPath("$.paymentLinkId").value("payment123"))
                .andExpect(jsonPath("$.orderId").exists())
                .andExpect(jsonPath("$.orderId").value("order123"));
    }

    @Test
    void createPaymentLink_invalidGateway() throws Exception {
        given(paymentService.createPaymentLink(any(), any(), any(), any()))
                .willThrow(new IllegalArgumentException("Unsupported payment gateway: invalid_gateway"));

        mvc.perform(post("/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "gateway": "invalid_gateway",
                            "orderId": "order123",
                            "amount": 1000,
                            "currency": "USD"
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Unsupported payment gateway: invalid_gateway"));
    }

    @Test
    void createPaymentLink_paymentServiceException() throws Exception {
        given(paymentService.createPaymentLink(any(), any(), any(), any()))
                .willThrow(new RuntimeException("Payment service error"));

        mvc.perform(post("/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "gateway": "stripe",
                            "orderId": "order123",
                            "amount": 1000,
                            "currency": "USD"
                        }
                        """))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Payment service error"));
    }

    @Test
    void createPaymentLink_invalidRequest() throws Exception {
        mvc.perform(post("/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "gateway": "stripe",
                            "orderId": "order123",
                            "currency": "USD"
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.amount").value("must not be null"));
    }
}