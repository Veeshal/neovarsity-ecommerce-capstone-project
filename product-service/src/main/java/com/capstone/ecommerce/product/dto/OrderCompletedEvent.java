package com.capstone.ecommerce.product.dto;

import java.util.List;

public record OrderCompletedEvent(List<ProductDto> orderItems) {}
