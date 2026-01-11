package com.capstone.ecommerce.order.dto;

import com.capstone.ecommerce.order.entity.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderDto(
        String orderId,
        int orderStatus,
        Long userId,
        Long addressId,
        BigDecimal totalAmount,
        String paymentLink,
        int paymentMethodId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static OrderDto from(final Order order) {
        return new OrderDto(
                order.getOrderId().toString(),
                order.getStatus().getId(),
                order.getUserId(),
                order.getDeliveryAddressId(),
                order.getTotalAmount(),
                order.getPaymentLink(),
                order.getPaymentMethod().getId(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}
