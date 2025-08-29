package com.capstone.ecommerce.order.client;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class PaymentClient {

    public String generatePayment(Long userId, UUID orderId, String paymentMethod, BigDecimal totalAmount) {
        // TODO: Logic to call the Payment Service and generate payment link
        return "http://payment-gateway.com/pay/" + orderId;
    }
}
