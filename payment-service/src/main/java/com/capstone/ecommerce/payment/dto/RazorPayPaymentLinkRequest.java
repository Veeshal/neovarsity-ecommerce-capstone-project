package com.capstone.ecommerce.payment.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL) // omit nulls from JSON
public record RazorPayPaymentLinkRequest(
        int amount, // mandatory
        String currency,
        @JsonProperty("accept_partial") Boolean acceptPartial,
        @JsonProperty("first_min_partial_amount") Integer firstMinPartialAmount,
        @JsonProperty("expire_by") Long expireBy,
        @JsonProperty("reference_id") String referenceId,
        String description,
        Customer customer,
        @JsonProperty("notify") Notification notification,
        @JsonProperty("reminder_enable") Boolean reminderEnable,
        Map<String, String> notes,
        @JsonProperty("callback_url") String callbackUrl,
        @JsonProperty("callback_method") String callbackMethod
) {
    // Compact constructor enforces mandatory param
    public RazorPayPaymentLinkRequest {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }

    // Nested records
    public record Customer(String name, String contact, String email) {}
    public record Notification(Boolean sms, Boolean email) {}

    // Builder-style helper
    public static class Builder {
        private final int amount; // mandatory
        private String currency;
        private Boolean acceptPartial;
        private Integer firstMinPartialAmount;
        private Long expireBy;
        private String referenceId;
        private String description;
        private Customer customer;
        private Notification notification;
        private Boolean reminderEnable;
        private Map<String, String> notes;
        private String callbackUrl;
        private String callbackMethod;

        public Builder(int amount) {
            if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
            this.amount = amount;
        }

        public Builder currency(String currency) { this.currency = currency; return this; }
        public Builder acceptPartial(Boolean acceptPartial) { this.acceptPartial = acceptPartial; return this; }
        public Builder firstMinPartialAmount(Integer firstMinPartialAmount) { this.firstMinPartialAmount = firstMinPartialAmount; return this; }
        public Builder expireBy(Long expireBy) { this.expireBy = expireBy; return this; }
        public Builder referenceId(String referenceId) { this.referenceId = referenceId; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder customer(Customer customer) { this.customer = customer; return this; }
        public Builder notification(Notification notification) { this.notification = notification; return this; }
        public Builder reminderEnable(Boolean reminderEnable) { this.reminderEnable = reminderEnable; return this; }
        public Builder notes(Map<String, String> notes) { this.notes = notes; return this; }
        public Builder callbackUrl(String callbackUrl) { this.callbackUrl = callbackUrl; return this; }
        public Builder callbackMethod(String callbackMethod) { this.callbackMethod = callbackMethod; return this; }

        public RazorPayPaymentLinkRequest build() {
            return new RazorPayPaymentLinkRequest(amount, currency, acceptPartial, firstMinPartialAmount,
                    expireBy, referenceId, description, customer, notification,
                    reminderEnable, notes, callbackUrl, callbackMethod);
        }
    }
}