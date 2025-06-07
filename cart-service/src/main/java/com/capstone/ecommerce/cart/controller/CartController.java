package com.capstone.ecommerce.cart.controller;

import com.capstone.ecommerce.cart.dto.AddToCartRequest;
import com.capstone.ecommerce.cart.dto.CheckoutRequest;
import com.capstone.ecommerce.cart.dto.CheckoutResponse;
import com.capstone.ecommerce.cart.entity.Cart;
import com.capstone.ecommerce.cart.entity.CheckoutInfo;
import com.capstone.ecommerce.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 *
 *
 * 1. Add to Cart: Users should be able to add products to their cart.
 * 2. Cart Review: View selected items in the cart with price, quantity, and total details.
 * 3. Checkout: Seamless process to finalize the purchase,
 *              including specifying delivery address and payment method.
 *
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("cart")
public class CartController {

    private final CartService cartService;

    @PostMapping
    public Cart addToCart(@RequestBody AddToCartRequest request) {
        return cartService.addToCart(request.userId(), request.productId(), request.quantity());
    }

    // TODO: Get userId from JWT token instead of request parameter
    @GetMapping
    public Cart getCart(@RequestParam(value = "userId") Long userId) {
        return cartService.getCart(userId);
    }

    @PostMapping("checkout")
    public CheckoutResponse checkout(@RequestBody CheckoutRequest request) {
        var info = cartService.checkout(request.userId(), request.addressId(), request.paymentMethodId());
        return new CheckoutResponse(info);
    }

}
