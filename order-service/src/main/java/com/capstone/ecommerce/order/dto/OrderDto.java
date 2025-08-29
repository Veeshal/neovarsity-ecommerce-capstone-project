package com.capstone.ecommerce.order.dto;

import com.capstone.ecommerce.order.entity.Order;

public record OrderDto(

) {
    public static OrderDto from(final Order order) {
        return null;
    }
}
