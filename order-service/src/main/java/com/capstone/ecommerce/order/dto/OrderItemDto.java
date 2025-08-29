package com.capstone.ecommerce.order.dto;

import com.capstone.ecommerce.order.entity.OrderItem;

import java.math.BigDecimal;

public record OrderItemDto(
        Long id,
        Long productId,
        String productName,
        Integer quantity,
        BigDecimal price
) {
    public static OrderItemDto from(final OrderItem item) {
        return new OrderItemDto(
                item.getId(),
                item.getProductId(),
                item.getProductName(),
                item.getQuantity(),
                item.getPrice()
        );
    }
}
