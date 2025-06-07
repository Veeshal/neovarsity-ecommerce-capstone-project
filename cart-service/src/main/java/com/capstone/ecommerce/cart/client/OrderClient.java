package com.capstone.ecommerce.cart.client;

import com.capstone.ecommerce.cart.entity.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class OrderClient {
    // TODO: Implement methods to interact with the Order Service

    public Order createOrder(Long userId, Long addressId, Long paymentMethodId) {

        return new Order(UUID.randomUUID().toString(), userId, addressId, BigDecimal.ZERO); // TODO: Replace null with actual total amount if available
    }

}
