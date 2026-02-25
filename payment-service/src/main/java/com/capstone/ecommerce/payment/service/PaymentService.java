package com.capstone.ecommerce.payment.service;

import com.capstone.ecommerce.payment.dto.Item;
import com.capstone.ecommerce.payment.dto.PaymentLinkInfo;
import com.capstone.ecommerce.payment.strategy.PaymentGatewayStrategy;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Service
public class PaymentService {
    private Map<String, PaymentGatewayStrategy> paymentGatewayStrategies;

    public PaymentLinkInfo createPaymentLink(String paymentGateway, String orderId, BigDecimal amount, String currency) {
        PaymentGatewayStrategy strategy = paymentGatewayStrategies.get(paymentGateway);
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported payment gateway: " + paymentGateway);
        }
        return strategy.createPaymentLink(orderId, amount, currency);
    }

    public void handleWebhook(String paymentGateway, String payload, String signature) {
        PaymentGatewayStrategy strategy = paymentGatewayStrategies.get(paymentGateway);
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported payment gateway: " + paymentGateway);
        }
        strategy.handleWebhook(payload, signature);
    }
}
