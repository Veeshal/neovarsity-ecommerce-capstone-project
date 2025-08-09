package com.capstone.ecommerce.cart.controller;

import com.capstone.ecommerce.cart.config.AppConfig;
import com.capstone.ecommerce.cart.service.CartService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import static com.capstone.ecommerce.cart.util.TestRequestFactory.createAddToCartRequestBody;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartController.class)
@Import(AppConfig.class)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CartService cartService;


    @Test
    void getCart() throws Exception {
        //        when(cartService.getCart(1L)).thenReturn(new Object()); // Replace with actual Cart object if needed
//        mockMvc.perform(get("/cart/1"))
//                .andExpect(status().isOk());
    }

    @Test
    void checkout() throws Exception {
//        when(cartService.checkout(1L)).thenReturn(true);
//        mockMvc.perform(post("/cart/1/checkout"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message").value("Checkout successful"));
    }
}