package com.capstone.ecommerce.cart.repository;

import com.capstone.ecommerce.cart.entity.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CartRepository extends MongoRepository<Cart, String> {
    Optional<Cart> findByUserId(Long userId);
}
