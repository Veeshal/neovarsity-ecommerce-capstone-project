package com.capstone.ecommerce.cart.controller;

import com.capstone.ecommerce.cart.dto.CartItemRequest;
import com.capstone.ecommerce.cart.entity.Cart;
import com.capstone.ecommerce.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("v1/cart/items")
public class CartItemController {

    private final CartService cartService;

    @PostMapping
    public Cart addItemToCart(@RequestBody CartItemRequest request) {
        log.debug("Add to cart: {}", request);
        // TODO: Get userId from JWT token instead of request body
        return cartService.addToCart(request.userId(), request.productId(), request.quantity());
    }

    @PutMapping("{productId}")
    public Cart updateCartItem(@PathVariable("productId") Long productId, @RequestBody CartItemRequest request) {
        log.debug("Update cart item: {}", request);
        // TODO: Get userId from JWT token instead of request body
        return cartService.updateCartItem(request.userId(), productId, request.quantity());
    }

    @DeleteMapping("{productId}")
    public Cart deleteCartItem(@PathVariable("productId") Long productId, @RequestParam("userId") Long userId) {
        log.debug("Delete cart item: {}", productId);
        // TODO: Get userId from JWT token instead of request parameter
        return cartService.deleteCartItem(userId, productId);
    }


}
