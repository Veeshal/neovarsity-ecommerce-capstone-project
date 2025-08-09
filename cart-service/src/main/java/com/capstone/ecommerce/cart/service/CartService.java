package com.capstone.ecommerce.cart.service;

import com.capstone.ecommerce.cart.client.OrderClient;
import com.capstone.ecommerce.cart.entity.Cart;
import com.capstone.ecommerce.cart.entity.CartItem;
import com.capstone.ecommerce.cart.entity.CheckoutInfo;
import com.capstone.ecommerce.cart.entity.Product;
import com.capstone.ecommerce.cart.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Slf4j
@RequiredArgsConstructor
@Service
public class CartService {

    private final OrderClient orderClient;
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

        // Add or update item in cart
        boolean found = false;
        for (CartItem item : cart.getItems()) {
            if (item.getProduct().getId().equals(productId)) {
                item.setQuantity(item.getQuantity() + quantity);
                found = true;
                break;
            }
        }
        if (!found) {
            Product product = new Product();
            product.setId(productId);
            product.setName("Product Name " + productId); // TODO: Placeholder, should fetch from product service
            CartItem newItem = new CartItem();
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            cart.getItems().add(newItem);
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

        for (CartItem item : cart.getItems()) {
            if (item.getProduct().getId().equals(productId)) {
                item.setQuantity(quantity);
                cartRepository.save(cart);
                return cart;
            }
        }

        throw new IllegalArgumentException("Product not found in cart");
    }

    @CachePut(value = "cart", key = "#userId")
    @Transactional
    public Cart deleteCartItem(Long userId, Long productId) {
        log.info("Deleting product {} from user {}'s cart", productId, userId);
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() ->
                new IllegalArgumentException("Cart not found for user " + userId));

        cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));
        cartRepository.save(cart);
        return cart;
    }

    @Cacheable(value = "cart", key = "#userId")
    public Cart getCart(Long userId) {
        log.info("Retrieving cart for user {}", userId);
        return cartRepository.findByUserId(userId).orElse(null);
    }

    public CheckoutInfo checkout(Long userId, Long addressId, int paymentMethodId) {
        // validate card items
        // TODO: Implement validation logic for cart items, e.g., check stock availability
        // TODO: Handle error cases, such as empty cart or invalid payment method


        // generate order
        var order = orderClient.createOrder(userId, addressId, paymentMethodId, getCart(userId));

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