package com.capstone.ecommerce.order.client;

import com.capstone.ecommerce.order.dto.Cart;
import com.capstone.ecommerce.order.dto.ValidateCartItem;
import com.capstone.ecommerce.order.dto.ValidateStockAvailabilityRequest;
import com.capstone.ecommerce.order.exception.StockValidationFailureException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ProductClient {

    private final RestClient restClient;

    public ProductClient(@Qualifier("productRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public void validateAllProducts(Cart cart) {
        var items = cart.items()
                .stream()
                .map(item -> new ValidateCartItem(item.product().id(), item.quantity()))
                .toList();

        restClient.post()
                .uri("/v1/product/validate-stock")
                .body(new ValidateStockAvailabilityRequest(items))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new StockValidationFailureException("Product validation failed");
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw new RuntimeException("Product service is unavailable");
                })
                .toBodilessEntity();
    }
}
