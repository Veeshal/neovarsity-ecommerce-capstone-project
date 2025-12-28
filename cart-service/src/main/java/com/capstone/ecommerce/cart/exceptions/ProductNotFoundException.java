package com.capstone.ecommerce.cart.exceptions;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Long productId) {
        super("Product not found with ID: " + productId);
    }
}
