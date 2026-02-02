package com.capstone.ecommerce.notification.strategy;

public interface MailStrategy {
    void send(String to, String subject, String body);
}
