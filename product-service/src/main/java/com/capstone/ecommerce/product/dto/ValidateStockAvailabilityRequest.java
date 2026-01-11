package com.capstone.ecommerce.product.dto;

import java.util.List;

public record ValidateStockAvailabilityRequest(
        List<ValidateCartItem> cartItems
) {
}
