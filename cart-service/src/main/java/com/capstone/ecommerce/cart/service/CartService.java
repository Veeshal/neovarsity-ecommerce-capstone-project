package com.capstone.ecommerce.cart.service;

import com.capstone.ecommerce.cart.client.OrderClient;
import com.capstone.ecommerce.cart.client.ProductClient;
import com.capstone.ecommerce.cart.client.UserClient;
import com.capstone.ecommerce.cart.entity.Cart;
import com.capstone.ecommerce.cart.entity.CartItem;
import com.capstone.ecommerce.cart.entity.CheckoutInfo;
import com.capstone.ecommerce.cart.exceptions.EmptyCartException;
import com.capstone.ecommerce.cart.exceptions.InvalidAddressException;
import com.capstone.ecommerce.cart.exceptions.InvalidPaymentMethodException;
import com.capstone.ecommerce.cart.exceptions.InvalidRequestedQuantityException;
import com.capstone.ecommerce.cart.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class CartService {

    private final OrderClient orderClient;
    private final ProductClient productClient;
    private final UserClient userClient;
    private final CartRepository cartRepository;

    @Transactional
    @CachePut(value = "cart", key = "#userId")
    public Cart addToCart(Long userId, Long productId, int quantity) {
        log.info("Adding product {} with quantity {} to user {}'s cart", productId, quantity, userId);
        Cart cart = cartRepository.findByUserId(userId).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUserId(userId);
            newCart.setItems(new ArrayList<>());
            return newCart;
        });

        var product = productClient.getProductById(productId);

        // Add or update item in cart
        var cartItem = cart.getItems()
                .stream()
                .filter(item -> Objects.equals(item.getProduct().getId(), productId))
                .findFirst();

        if (cartItem.isEmpty()) {

            if (quantity > product.getQuantity()) {
                throw InvalidRequestedQuantityException.getInstance(quantity, productId);
            }
            CartItem newItem = new CartItem();
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            cart.getItems().add(newItem);

        } else {

            var item = cartItem.get();
            if (item.getQuantity() + quantity > product.getQuantity()) {
                throw InvalidRequestedQuantityException
                        .getInstance(item.getQuantity() + quantity, productId);
            }
            item.setQuantity(item.getQuantity() + quantity);
        }


        cartRepository.save(cart);
        return cart;
    }

    @CachePut(value = "cart", key = "#userId")
    @Transactional
    public Cart updateCartItem(Long userId, Long productId, int quantity) {
        log.info("Updating product {} with quantity {} in user {}'s cart", productId, quantity, userId);
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() ->
                new IllegalArgumentException("Cart not found for user " + userId));

        var cartItem = cart.getItems().stream()
                .filter(item -> Objects.equals(item.getProduct().getId(), productId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Product not found in cart"));

        var product = productClient.getProductById(productId);
        if (quantity > product.getQuantity()) {
            throw InvalidRequestedQuantityException.getInstance(quantity, productId);
        }

        cartItem.setQuantity(quantity);
        return cartRepository.save(cart);
    }

    @CachePut(value = "cart", key = "#userId")
    @Transactional
    public Cart deleteCartItem(Long userId, Long productId) {
        log.info("Deleting product {} from user {}'s cart", productId, userId);
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() ->
                new IllegalArgumentException("Cart not found for user " + userId));


        boolean removed = cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));
        log.info("Item removed: {} {}", productId, removed);
        if (removed) cartRepository.save(cart);
        return cart;
    }

    @Cacheable(value = "cart", key = "#userId")
    public Cart getCart(Long userId) {
        log.info("Retrieving cart for user {}", userId);
        return cartRepository.findByUserId(userId).orElse(null);
    }

    public CheckoutInfo checkout(Long userId, Long addressId, int paymentMethodId) {

        // Validate payment method
        var paymentMethods = List.of(4, 5, 6);
        if (!paymentMethods.contains(paymentMethodId)) {
            throw InvalidPaymentMethodException.forPaymentMethodId(paymentMethodId);
        }

        // validate cart
        var cart = cartRepository.findByUserId(userId).orElse(null);
        if (cart == null || cart.getItems().isEmpty()) {
            throw EmptyCartException.forUserId(userId);
        }

        // validate address
        if (!userClient.isValidAddress(userId, addressId)) {
            throw InvalidAddressException.forAddressId(addressId, userId);
        }

        // generate order
        var order = orderClient.createOrder(userId, addressId, paymentMethodId, cart);

        return new CheckoutInfo(
                order.orderId(),
                userId,
                addressId,
                paymentMethodId,
                order.paymentLink()
        );
    }

    @CacheEvict(value = "cart", key = "#userId")
    @Transactional
    public void clearCart(Long userId) {
        log.info("Clearing cart for user {}", userId);
        cartRepository.deleteByUserId(userId);
    }
}