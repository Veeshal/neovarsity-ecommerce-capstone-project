package com.capstone.ecommerce.notification.service;

import com.capstone.ecommerce.notification.entity.EmailTemplate;
import com.capstone.ecommerce.notification.entity.EmailTemplateType;
import com.capstone.ecommerce.notification.entity.SmsTemplate;
import com.capstone.ecommerce.notification.entity.SmsTemplateType;
import com.capstone.ecommerce.notification.repository.EmailTemplateRepository;
import com.capstone.ecommerce.notification.repository.SmsTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class TemplateService {

    private final EmailTemplateRepository emailTemplateRepository;
    private final SmsTemplateRepository smsTemplateRepository;

    public EmailTemplate updateEmailTemplate(EmailTemplateType type, EmailTemplate emailTemplate, Long userId) {

        var template = emailTemplateRepository.findLatest(type)
                .orElseThrow(() -> new RuntimeException("Email template not found for type: " + type));

        template.setSubject(emailTemplate.getSubject());
        template.setBody(emailTemplate.getBody());
        template.setVersion(template.getVersion() + 1);
        template.setCreatedBy(userId);

        return emailTemplateRepository.save(template);
    }


    public SmsTemplate updateSmsTemplate(SmsTemplateType type, SmsTemplate smsTemplate, Long userId) {

        var template = smsTemplateRepository.findLatest(type)
                .orElseThrow(() -> new RuntimeException("Email template not found for type: " + type));

        template.setBody(smsTemplate.getBody());
        template.setVersion(template.getVersion() + 1);
        template.setCreatedBy(userId);
        return smsTemplateRepository.save(template);
    }
}
