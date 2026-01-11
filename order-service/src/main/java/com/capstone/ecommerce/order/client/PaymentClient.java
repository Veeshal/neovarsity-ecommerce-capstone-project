package com.capstone.ecommerce.order.client;

import com.capstone.ecommerce.order.dto.Item;
import com.capstone.ecommerce.order.dto.PaymentLinkGenerateRequest;
import com.capstone.ecommerce.order.dto.PaymentLinkGenerateResponse;
import com.capstone.ecommerce.order.entity.Order;
import com.capstone.ecommerce.order.entity.PaymentMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@Component
public class PaymentClient {

    @Value("${ecom.client.payment-service.url}")
    private String paymentServiceBaseUrl;

    private final RestTemplate restTemplate;

    public PaymentLinkGenerateResponse generatePayment(Order order, PaymentMethod payment, BigDecimal totalAmount) {

        String orderId = order.getOrderId().toString();
        String currency = "USD"; // Assuming USD for simplicity
        String gateway = payment.getCode();
        List<Item> items = order.getItems().stream()
                .map(item -> new Item(
                        item.getProductName(),
                        item.getQuantity(),
                        item.getPrice().doubleValue()))
                .toList();

        var request = new PaymentLinkGenerateRequest(
                gateway,
                items,
                currency,
                orderId
        );

        var response = restTemplate.postForEntity(paymentServiceBaseUrl + "/v1/payments", request,
                PaymentLinkGenerateResponse.class);

        return response.getBody();
    }
}
