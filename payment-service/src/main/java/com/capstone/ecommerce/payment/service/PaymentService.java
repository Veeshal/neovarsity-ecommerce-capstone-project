package com.capstone.ecommerce.payment.service;

import com.capstone.ecommerce.payment.strategy.PaymentGatewayStrategy;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@AllArgsConstructor
@Service
public class PaymentService {
    private Map<String, PaymentGatewayStrategy> paymentGatewayStrategies;

    public String createPaymentLink(String paymentGateway, String paymentDetails) {
        PaymentGatewayStrategy strategy = paymentGatewayStrategies.get(paymentGateway);
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported payment gateway: " + paymentGateway);
        }
        return strategy.createPaymentLink(paymentDetails);
    }

    public void handleWebhook(String paymentGateway, HttpServletRequest request) {
        PaymentGatewayStrategy strategy = paymentGatewayStrategies.get(paymentGateway);
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported payment gateway: " + paymentGateway);
        }
        strategy.handleWebhook(request);
    }
}
