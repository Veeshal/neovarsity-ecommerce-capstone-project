package com.capstone.ecommerce.payment.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RazorPayPaymentLinkResponse(
        @JsonProperty("accept_partial") Boolean acceptPartial,
        int amount,
        @JsonProperty("amount_paid") int amountPaid,
        @JsonProperty("callback_method") String callbackMethod,
        @JsonProperty("callback_url") String callbackUrl,
        @JsonProperty("cancelled_at") Long cancelledAt,
        @JsonProperty("created_at") Long createdAt,
        String currency,
        List<Customer> customer,
        String description,
        @JsonProperty("expire_by") Long expireBy,
        @JsonProperty("expired_at") Long expiredAt,
        @JsonProperty("first_min_partial_amount") Integer firstMinPartialAmount,
        String id,
        Map<String, String> notes, // flexible key-value pairs
        @JsonProperty("notify") Notification notification,
        Object payments,
        @JsonProperty("reference_id") String referenceId,
        @JsonProperty("reminder_enable") Boolean reminderEnable,
        List<Object> reminders,
        @JsonProperty("short_url") String shortUrl,
        PaymentLinkStatus status,
        @JsonProperty("updated_at") Long updatedAt,
        @JsonProperty("user_id") String userId
) {
    public record Customer(String contact, String email, String name) {}
    public record Notification(Boolean email, Boolean sms) {}
}