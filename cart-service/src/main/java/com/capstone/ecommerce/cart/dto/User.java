package com.capstone.ecommerce.cart.dto;

import java.util.List;

public record User(
        String name,
        String email,
        List<Address> address
) {
}

