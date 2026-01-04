package com.capstone.ecommerce.cart.client;

import com.capstone.ecommerce.cart.entity.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;


@Slf4j
@Service
public class ProductClient {

    private final RestClient restClient;

    public ProductClient(@Qualifier("productRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public Product getProductById(Long productId) {

        log.info("Fetching product with ID: {}", productId);

        return restClient.get()
                .uri("/v1/product/{id}", productId)
                .retrieve()
                // TODO: Improve error handling
                .body(Product.class);
    }


}
