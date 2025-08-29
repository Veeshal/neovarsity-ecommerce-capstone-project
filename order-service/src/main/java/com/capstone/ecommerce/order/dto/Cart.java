package com.capstone.ecommerce.order.dto;

import java.util.List;

public record Cart(
        List<CartItem> items
) { }
