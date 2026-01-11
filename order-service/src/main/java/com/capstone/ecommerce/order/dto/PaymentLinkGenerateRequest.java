package com.capstone.ecommerce.order.dto;

import java.util.List;

public record PaymentLinkGenerateRequest(
    String gateway,
    List<Item> items,
    String currency,
    String orderId
) {
}

