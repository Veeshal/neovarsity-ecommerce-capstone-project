package com.capstone.ecommerce.cart.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentMethod {
    STRIPE(4, "STRIPE", "Stripe"),
    RAZORPAY(5, "RAZORPAY", "Razorpay"),
    CASH_ON_DELIVERY(9, "CASH_ON_DELIVERY", "Cash on Delivery");

    private final int id;
    private final String code;
    private final String description;


    boolean isValidPaymentMethod(int id) {
        for (PaymentMethod method : PaymentMethod.values()) {
            if (method.id == id) {
                return true;
            }
        }
        return false;
    }
}
