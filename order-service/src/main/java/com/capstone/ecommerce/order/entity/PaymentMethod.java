package com.capstone.ecommerce.order.entity;

import com.capstone.ecommerce.order.exception.InvalidPaymentMethodException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum PaymentMethod {
    STRIPE(4, "stripe", "Stripe"),
    RAZORPAY(5, "razor-pay", "Razorpay"),
    CASH_ON_DELIVERY(9, "cod", "Cash on delivery");

    private final int id;
    private final String code;
    private final String description;


    public static PaymentMethod from(int id) {
        return Arrays.stream(PaymentMethod.values())
                .filter(pm -> pm.id == id)
                .findFirst()
                .orElseThrow(() -> new InvalidPaymentMethodException(String.valueOf(id)));
    }

    public static PaymentMethod from(String code) {
        return Arrays.stream(PaymentMethod.values())
                .filter(pm -> pm.code.equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new InvalidPaymentMethodException(code));
    }

    boolean isValidPaymentMethod(int id) {
        for (PaymentMethod method : PaymentMethod.values()) {
            if (method.id == id) {
                return true;
            }
        }
        return false;
    }
}
