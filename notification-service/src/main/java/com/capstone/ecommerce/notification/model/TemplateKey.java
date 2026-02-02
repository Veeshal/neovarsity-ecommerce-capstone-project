package com.capstone.ecommerce.notification.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TemplateKey {
    USER_NAME("userName", "Name of the user", "{{userName}}"),
    FIRST_NAME("firstName", "First name of the user", "{{firstName}}"),
    LAST_NAME("lastName", "Last name of the user", "{{lastName}}"),
    PASSWORD_RESET_CODE("passwordResetCode", "Code to reset password", "{{passwordResetCode}}"),
    ORDER_ID("orderId", "Order identification number", "{{orderId}}"),
    ORDER_DATE("orderDate", "Date when the order was placed", "{{orderDate}}");

    private final String key;
    private final String description;
    private final String value;

}
