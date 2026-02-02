package com.capstone.ecommerce.notification.service;

import com.capstone.ecommerce.notification.entity.EmailTemplate;

public class MailTestUtil {
    public static EmailTemplate createEmailTemplate(String subject, String body) {
        EmailTemplate emailTemplate = new EmailTemplate();
        emailTemplate.setSubject(subject);
        emailTemplate.setBody(body);
        return emailTemplate;
    }
}
