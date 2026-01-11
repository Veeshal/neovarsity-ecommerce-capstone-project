package com.capstone.ecommerce.order.dto;

import java.util.List;

public record ValidateStockAvailabilityRequest(
        List<ValidateCartItem> cartItems
) {
}
