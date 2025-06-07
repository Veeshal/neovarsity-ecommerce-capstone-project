package com.capstone.ecommerce.cart.entity;

import java.math.BigDecimal;

public record Order(String orderId, Long userId, Long addressId, BigDecimal totalAmount) {
}
