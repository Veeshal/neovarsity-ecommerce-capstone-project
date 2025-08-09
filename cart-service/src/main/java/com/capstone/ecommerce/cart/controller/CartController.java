package com.capstone.ecommerce.cart.controller;

import com.capstone.ecommerce.cart.dto.CheckoutRequest;
import com.capstone.ecommerce.cart.dto.CheckoutResponse;
import com.capstone.ecommerce.cart.entity.Cart;
import com.capstone.ecommerce.cart.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("v1/cart")
public class CartController {

    private final CartService cartService;

    @GetMapping
    public Cart getCart(@RequestParam(value = "userId") Long userId) {
        // TODO: Get userId from JWT token instead of request parameter
        return cartService.getCart(userId);
    }

    @PostMapping("checkout")
    public CheckoutResponse checkout(@Valid @RequestBody CheckoutRequest request) {
        // TODO: Get userId from JWT token instead of request parameter
        var info = cartService.checkout(request.userId(), request.addressId(), request.paymentMethodId());
        return new CheckoutResponse(info);
    }

}
