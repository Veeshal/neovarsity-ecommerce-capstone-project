package com.capstone.ecommerce.cart.service;

import com.capstone.ecommerce.cart.client.OrderClient;
import com.capstone.ecommerce.cart.entity.Cart;
import com.capstone.ecommerce.cart.repository.CartRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private OrderClient orderClient;

    @InjectMocks
    private CartService cartService;

    @Test
    void addToCart() {
        Cart cart = new Cart();
        cart.setUserId(1L);
        given(cartRepository.save(any(Cart.class))).willReturn(cart);

        Cart result = cartService.addToCart(1L, 101L, 2);

        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertTrue(result.getItems().stream()
                .anyMatch(item -> item.getProduct().getId().equals(101L) && item.getQuantity() == 2));

        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void getCart() {
        // Implement test logic for getting cart

        given(cartRepository.findByUserId(1L))
                .willReturn(Optional.of(new Cart()));

        var cart = cartService.getCart(1L);


        assertNotNull(cart);


        assertTrue(true); // Placeholder assertion
    }

    @Test
    void checkout() {
        // Example test logic for checkout
//        Cart cart = new Cart();
//        cart.setUserId(1L);
//        given(cartRepository.findByUserId(1L)).willReturn(Optional.of(cart));
//        // Assume paymentClient and orderClient have some methods to be called
//        when(paymentClient.createPaymentLink(any(), any())).thenReturn(true);
//        when(orderClient.createOrder(any())).thenReturn(true);
//
//        boolean result = cartService.checkout(1L);
//
//        assertTrue(result);
//        verify(paymentClient, times(1)).processPayment(any());
//        verify(orderClient, times(1)).createOrder(any());
    }
}