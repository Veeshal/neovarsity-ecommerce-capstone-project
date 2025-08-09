package com.capstone.ecommerce.cart.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class TestRequestFactory {
    public static String createAddToCartRequestBody(ObjectMapper objectMapper, long productId, int quantity) {
        ObjectNode requestNode = objectMapper.createObjectNode();
        requestNode.put("productId", productId);
        requestNode.put("quantity", quantity);
        requestNode.put("userId", 1L);
        try {
            return objectMapper.writeValueAsString(requestNode);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



}

