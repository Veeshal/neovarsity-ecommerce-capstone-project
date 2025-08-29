package com.capstone.ecommerce.order.dto;

import com.capstone.ecommerce.order.entity.Order;
import com.capstone.ecommerce.order.entity.OrderStatus;
import com.capstone.ecommerce.order.entity.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderDto(
        String orderId,
        Long userId,
        Long addressId,
        BigDecimal totalAmount,
        String paymentLink,
        OrderStatus orderStatus,
        PaymentMethod paymentMethod,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static OrderDto from(final Order order) {
        return new OrderDto(
                order.getOrderId().toString(),
                order.getUserId(),
                order.getDeliveryAddressId(),
                order.getTotalAmount(),
                order.getPaymentLink(),
                order.getStatus(),
                order.getPaymentMethod(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}
