package com.capstone.ecommerce.cart.client;

import com.capstone.ecommerce.cart.dto.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Objects;

@Slf4j
@Service
public class UserClient {
    private final RestClient restClient;

    public UserClient(@Qualifier("userRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public User getUserById(Long userId) {

        log.info("Fetching user with ID: {}", userId);

        return restClient.get()
                .uri("/v1/users")
                .retrieve()
                .body(User.class);
    }

    public boolean isValidAddress(Long userId, Long addressId) {
        log.info("Validating address {} for user {}", addressId, userId);

        var user = getUserById(userId);
        return user.address()
                .stream()
                .anyMatch((address) -> Objects.equals(address.id(), addressId));
    }
}
