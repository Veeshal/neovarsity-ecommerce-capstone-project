package com.capstone.ecommerce.cart.controller;

import com.capstone.ecommerce.cart.dto.CheckoutRequest;
import com.capstone.ecommerce.cart.dto.CheckoutResponse;
import com.capstone.ecommerce.cart.entity.Cart;
import com.capstone.ecommerce.cart.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("v1/cart")
public class CartController {

    private final CartService cartService;

    @Value("${ecom.claims.userId}")
    private String USER_ID_CLAIM;

    @GetMapping
    public Cart getCart(@AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim(USER_ID_CLAIM);
        return cartService.getCart(userId);
    }

    @PostMapping("checkout")
    public CheckoutResponse checkout(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CheckoutRequest request) {
        Long userId = jwt.getClaim(USER_ID_CLAIM);
        var info = cartService.checkout(userId, request.addressId(), request.paymentMethodId());
        return new CheckoutResponse(info);
    }

}
