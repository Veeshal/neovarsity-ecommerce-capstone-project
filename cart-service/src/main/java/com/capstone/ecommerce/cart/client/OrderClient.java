package com.capstone.ecommerce.cart.client;

import com.capstone.ecommerce.cart.dto.OrderCreationRequestDto;
import com.capstone.ecommerce.cart.entity.Cart;
import com.capstone.ecommerce.cart.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Component
public class OrderClient {

    @Value("${ecom.client.order-service.url}")
    private String orderServiceBaseUrl;

    private final RestTemplate restTemplate;

    public Order createOrder(Long userId, Long addressId, Integer paymentMethodId, Cart cart) {

        var request = new OrderCreationRequestDto(userId, addressId, paymentMethodId, cart);
        try {
            var response = restTemplate.postForEntity(orderServiceBaseUrl + "/api/v1/order", request, Order.class);
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create order: " + e.getMessage(), e);
        }
    }

}
