package com.capstone.ecommerce.cart.dto;

public record CheckoutRequest(Long userId, Long addressId, Long paymentMethodId) {
}
