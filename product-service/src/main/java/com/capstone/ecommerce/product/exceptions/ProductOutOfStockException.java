package com.capstone.ecommerce.product.exceptions;

public class ProductOutOfStockException extends RuntimeException {

    public ProductOutOfStockException(Long productId) {
        super("Product with ID " + productId + " is out of stock.");
    }
}
