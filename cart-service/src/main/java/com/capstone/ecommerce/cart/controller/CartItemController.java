package com.capstone.ecommerce.cart.controller;

import com.capstone.ecommerce.cart.dto.CartItemRequest;
import com.capstone.ecommerce.cart.entity.Cart;
import com.capstone.ecommerce.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

@Slf4j
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("v1/cart/items")
public class CartItemController {

    private final CartService cartService;

    @Value("${ecom.claims.userId}")
    private String USER_ID_CLAIM;

    @Value("${ecom.claims.email}")
    private String EMAIL_CLAIM;

    @Value("${ecom.claims.role}")
    private String ROLE_CLAIM;

    @PostMapping
    public Cart addItemToCart(@AuthenticationPrincipal Jwt jwt, @RequestBody CartItemRequest request) {
        log.debug("Add to cart: {}", request);
        Long userId = jwt.getClaim(USER_ID_CLAIM);
        return cartService.addToCart(userId, request.productId(), request.quantity());
    }

    @PutMapping
    public Cart updateCartItem(@AuthenticationPrincipal Jwt jwt, @RequestBody CartItemRequest request) {
        log.debug("Update cart item: {}", request);
        Long userId = jwt.getClaim(USER_ID_CLAIM);
        return cartService.updateCartItem(userId, request.productId(), request.quantity());
    }

    @DeleteMapping("{productId}")
    public Cart deleteCartItem(@AuthenticationPrincipal Jwt jwt, Long productId) {
        log.debug("Delete cart item: {}", productId);
        Long userId = jwt.getClaim(USER_ID_CLAIM);
        return cartService.deleteCartItem(userId, productId);
    }


}
