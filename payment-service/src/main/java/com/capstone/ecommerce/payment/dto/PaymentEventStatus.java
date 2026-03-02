package com.capstone.ecommerce.payment.dto;

public enum PaymentEventStatus {

    /************************************************
     * Payment link generated and sent to customer
     ************************************************/
    PAYMENT_LINK_PAID, // set order placed
    PAYMENT_LINK_EXPIRED, //
    PAYMENT_LINK_CANCELLED, //

    /*************************************************
     * Payment initiated by customer
     *************************************************/
    PAYMENT_COMPLETED, // send notification
    PAYMENT_FAILED, // send notification

    /*************************************************
     * Refund initiated by customer or system
     *************************************************/
    REFUND_INITIATED, // send notification
    REFUND_COMPLETED, // send notification & updated order status to refunded
    REFUND_FAILED, // send notification

    /**************************************************
     * Partial refund initiated by customer or system
     **************************************************/
    PARTIAL_REFUND_INITIATED, // send notification
    PARTIAL_REFUND_COMPLETED, // send notification & updated order status to partially refunded
    PARTIAL_REFUND_FAILED // send notification
}
