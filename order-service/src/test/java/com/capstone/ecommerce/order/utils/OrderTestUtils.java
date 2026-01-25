package com.capstone.ecommerce.order.utils;

import com.capstone.ecommerce.order.dto.Cart;
import com.capstone.ecommerce.order.dto.CartItem;
import com.capstone.ecommerce.order.dto.PaymentLinkGenerateResponse;
import com.capstone.ecommerce.order.dto.Product;

import java.util.List;

public class OrderTestUtils {
    public static Cart createCartWithItems() {
        return new Cart(List.of(
                new CartItem(new Product(1L, "item1", 10.0), 2),
                new CartItem(new Product(2L, "item2", 20.0), 1)
        ));
    }

    public static Cart createEmptyCart() {
        return new Cart(List.of());
    }

    public static PaymentLinkGenerateResponse createPaymentLinkResponse() {
        return new PaymentLinkGenerateResponse("http://payment-link.com",
                System.currentTimeMillis() + 3600000,
                "http://redirect-url.com", "payment123", "order123");
    }
}
