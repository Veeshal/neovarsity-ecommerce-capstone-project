package com.capstone.ecommerce.payment.service;

import com.capstone.ecommerce.payment.dto.PaymentLinkInfo;
import com.capstone.ecommerce.payment.adapter.PaymentGatewayAdapter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@AllArgsConstructor
@Service
public class PaymentService {
    private Map<String, PaymentGatewayAdapter> paymentGatewayAdapters;

    public PaymentLinkInfo createPaymentLink(String paymentGateway, String orderId, BigDecimal amount, String currency) {
        PaymentGatewayAdapter adapter = paymentGatewayAdapters.get(paymentGateway);
        if (adapter == null) {
            throw new IllegalArgumentException("Unsupported payment gateway: " + paymentGateway);
        }
        return adapter.createPaymentLink(orderId, amount, currency);
    }

    public void handleWebhook(String paymentGateway, String payload, String signature) {
        PaymentGatewayAdapter adapter = paymentGatewayAdapters.get(paymentGateway);
        if (adapter == null) {
            throw new IllegalArgumentException("Unsupported payment gateway: " + paymentGateway);
        }
        adapter.handleWebhook(payload, signature);
    }
}
