package com.capstone.ecommerce.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record QuantityUpdateDto(

        @Schema(
                description = "ID of the product whose stock will be updated",
                example = "1001"
        )
        Long productId,


        @Schema(
                description = "Quantity change. Positive values increase stock, negative values decrease stock.",
                requiredMode = Schema.RequiredMode.REQUIRED,
                minimum = "-1000",
                maximum = "1000"
        )
        Integer quantity
) {}
