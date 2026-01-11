package com.capstone.ecommerce.cart.client;

import com.capstone.ecommerce.cart.dto.OrderCreationRequestDto;
import com.capstone.ecommerce.cart.entity.Cart;
import com.capstone.ecommerce.cart.entity.Order;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class OrderClient {

    private final RestClient restClient;

    public OrderClient(@Qualifier("orderRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public Order createOrder(Long addressId, Integer paymentMethodId, Cart cart) {

        var request = new OrderCreationRequestDto(addressId, paymentMethodId, cart);

        return restClient.post()
                .uri("/v1/orders")
                .body(request)
                .retrieve()
                .body(Order.class);
    }

}
