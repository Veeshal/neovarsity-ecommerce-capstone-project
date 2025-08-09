package com.capstone.ecommerce.cart.controller;

import com.capstone.ecommerce.cart.config.AppConfig;
import com.capstone.ecommerce.cart.service.CartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.capstone.ecommerce.cart.util.TestRequestFactory.createAddToCartRequestBody;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartItemController.class)
@Import(AppConfig.class)
class CartItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CartService cartService;


    @Test
    void addItemToCart() throws Exception {
        String requestBody = createAddToCartRequestBody(objectMapper, 1L, 2);

//        mockMvc.perform(post("/api/v1/cart/items")
//                        .contentType("application/json")
//                        .content(requestBody))
//                .andExpect(status().isOk());
//              .andExpect(jsonPath("$.message").value("Product added to cart successfully"));
    }

    @Test
    void updateCartItem() {
    }

    @Test
    void deleteCartItem() {
    }
}