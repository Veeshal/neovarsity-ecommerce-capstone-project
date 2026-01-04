package com.capstone.ecommerce.cart.service;

import com.capstone.ecommerce.cart.client.OrderClient;
import com.capstone.ecommerce.cart.client.ProductClient;
import com.capstone.ecommerce.cart.client.UserClient;
import com.capstone.ecommerce.cart.entity.*;
import com.capstone.ecommerce.cart.exceptions.EmptyCartException;
import com.capstone.ecommerce.cart.exceptions.InvalidAddressException;
import com.capstone.ecommerce.cart.exceptions.InvalidPaymentMethodException;
import com.capstone.ecommerce.cart.exceptions.InvalidRequestedQuantityException;
import com.capstone.ecommerce.cart.repository.CartRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock private CartRepository cartRepository;
    @Mock private OrderClient orderClient;
    @Mock private UserClient userClient;
    @Mock private ProductClient productClient;

    @InjectMocks
    private CartService cartService;

    @Test
    void addToCart_Success() {

        var userId = 1L;
        var productId = 101L;
        var quantity = 2;

        var cart = new Cart();
        cart.setUserId(userId);

        var product = new Product();
        product.setId(productId);
        product.setName("Test Product");
        product.setQuantity(10);

        given(cartRepository.save(any(Cart.class))).willReturn(cart);
        given(productClient.getProductById(productId)).willReturn(product);

        Cart result = cartService.addToCart(userId, productId, quantity);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        var cartItem = result.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow();

        assertEquals(quantity, cartItem.getQuantity());

        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void addToCart_SuccessExistingItem() {

        var userId = 1L;
        var productId = 101L;
        var productQuantity = 10;

        var initialQuantity = 2;
        var additionalQuantity = 3;

        var product = new Product();
        product.setId(productId);
        product.setQuantity(productQuantity);

        var cart = new Cart();
        cart.setUserId(userId);
        cart.setItems(List.of(new CartItem()));
        cart.getItems().getFirst().setProduct(new Product());
        cart.getItems().getFirst().getProduct().setId(productId);
        cart.getItems().getFirst().setQuantity(initialQuantity); // Initial quantity


        given(productClient.getProductById(productId)).willReturn(product);
        given(cartRepository.findByUserId(userId)).willReturn(Optional.of(cart));
        given(cartRepository.save(any(Cart.class))).willReturn(cart);

        var result = assertDoesNotThrow(() -> cartService.addToCart(userId, productId, additionalQuantity));

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        var cartItem = result.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow();

        assertEquals(initialQuantity + additionalQuantity, cartItem.getQuantity());

        verify(cartRepository, times(1)).save(any(Cart.class));
    }


    @Test
    void updateCartItem_Success() {

        var userId = 1L;
        var productId = 101L;
        var productQuantity = 10;
        var requestQuantity = 5;

        var product = new Product();
        product.setId(productId);
        product.setQuantity(productQuantity);

        var cart = new Cart();
        cart.setUserId(userId);
        cart.setItems(List.of(new CartItem()));

        product.setId(productId);
        cart.getItems().getFirst().setProduct(new Product());
        cart.getItems().getFirst().getProduct().setId(productId);
        cart.getItems().getFirst().setQuantity(2); // Initial quantity



        given(productClient.getProductById(productId)).willReturn(product);
        given(cartRepository.findByUserId(userId)).willReturn(Optional.of(cart));
        given(cartRepository.save(any(Cart.class))).willReturn(cart);

        Cart result = cartService.updateCartItem(userId, productId, requestQuantity);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertTrue(result.getItems().stream()
                .anyMatch(item -> item.getProduct().getId().equals(productId) && item.getQuantity() == requestQuantity));

        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void updateCartItem_FailureInvalidProduct() {


        var userId = 1L;
        var productId = 101L;
        var quantity = 5;

        var cart = new Cart();
        cart.setUserId(userId);
        cart.setItems(List.of());

        given(cartRepository.findByUserId(userId)).willReturn(Optional.of(cart));

        assertThrows(IllegalArgumentException.class, () ->
                cartService.updateCartItem(userId, productId, quantity));

        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void updateCartItem_FailureInvalidQuantity() {


        var userId = 1L;
        var productId = 101L;
        var productQuantity = 5;
        var requestQuantity = 10;

        var product = new Product();
        product.setId(productId);
        product.setQuantity(productQuantity);

        var cart = new Cart();
        cart.setUserId(userId);
        cart.setItems(List.of(new CartItem()));

        product.setId(productId);
        cart.getItems().getFirst().setProduct(new Product());
        cart.getItems().getFirst().getProduct().setId(productId);
        cart.getItems().getFirst().setQuantity(2); // Doesn't matter for this test

        given(productClient.getProductById(productId)).willReturn(product);
        given(cartRepository.findByUserId(userId)).willReturn(Optional.of(cart));

        assertThrows(InvalidRequestedQuantityException.class, () ->
                cartService.updateCartItem(userId, productId, requestQuantity));

        verify(cartRepository, never()).save(any(Cart.class));
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
    void checkout_Success() {

        long userId = 1L;
        long addressId = 1L;
        int paymentMethodId = 4;

        var cart = mock(Cart.class);
        var order = mock(Order.class);

        when(cart.getItems()).thenReturn(List.of(new CartItem()));

        given(cartRepository.findByUserId(userId)).willReturn(Optional.of(cart));
        // Assume paymentClient and orderClient have some methods to be called
        when(userClient.isValidAddress(userId, addressId)).thenReturn(true);
        when(orderClient.createOrder(userId, addressId, paymentMethodId, cart)).thenReturn(order);

        var result = cartService.checkout(userId, addressId, paymentMethodId);

        assertNotNull(result);

        verify(userClient, times(1))
                .isValidAddress(userId, addressId);

        verify(orderClient, times(1))
                .createOrder(userId, addressId, paymentMethodId, cart);
    }

    @Test
    void checkout_FailureWithEmptyCart() {
        // Example test logic for checkout with empty cart
        long userId = 1L;
        long addressId = 1L;
        int paymentMethodId = 4;

        given(cartRepository.findByUserId(userId)).willReturn(Optional.empty());

        assertThrows(EmptyCartException.class, () ->
                cartService.checkout(userId, addressId, paymentMethodId));


        verify(userClient, never()).isValidAddress(eq(userId), eq(addressId));
        verify(orderClient, never()).createOrder(eq(userId), eq(addressId), eq(paymentMethodId), any());
    }

    @Test
    void checkout_FailureWithInvalidAddress() {
        long userId = 1L;
        long addressId = 1L;
        int paymentMethodId = 4;

        var cart = mock(Cart.class);
        given(cart.getItems()).willReturn(List.of(new CartItem()));

        given(cartRepository.findByUserId(userId)).willReturn(Optional.of(cart));
        given(userClient.isValidAddress(userId, addressId)).willReturn(false);

        assertThrows(InvalidAddressException.class, () ->
                cartService.checkout(userId, addressId, paymentMethodId));

        verify(orderClient, never())
                .createOrder(userId, addressId, paymentMethodId, cart);
    }

    @Test
    void checkout_FailureWithInvalidPaymentMethod() {
        long userId = 1L;
        long addressId = 1L;
        int paymentMethodId = 99; // Invalid payment method

        assertThrows(InvalidPaymentMethodException.class, () ->
                cartService.checkout(userId, addressId, paymentMethodId));

    }
}