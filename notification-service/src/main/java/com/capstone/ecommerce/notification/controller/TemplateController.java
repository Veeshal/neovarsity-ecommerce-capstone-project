package com.capstone.ecommerce.notification.controller;

import com.capstone.ecommerce.notification.entity.EmailTemplate;
import com.capstone.ecommerce.notification.entity.EmailTemplateType;
import com.capstone.ecommerce.notification.entity.SmsTemplate;
import com.capstone.ecommerce.notification.entity.SmsTemplateType;
import com.capstone.ecommerce.notification.repository.EmailTemplateRepository;
import com.capstone.ecommerce.notification.repository.SmsTemplateRepository;
import com.capstone.ecommerce.notification.service.TemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("v1")
public class TemplateController {

    @Value("${ecom.claims.userId}")
    private String USER_ID_CLAIM;

    @Value("${ecom.claims.email}")
    private String EMAIL_CLAIM;

    @Value("${ecom.claims.role}")
    private String ROLE_CLAIM;

    private final EmailTemplateRepository emailTemplateRepository;
    private final SmsTemplateRepository smsTemplateRepository;

    private final TemplateService templateService;

    @GetMapping("/template/email")
    public EmailTemplate getEmailTemplate(@RequestParam(value = "type") EmailTemplateType type) {
        return emailTemplateRepository.findLatest(type)
                .orElseThrow(() -> new RuntimeException("Email template not found for type: " + type));
    }

    @PutMapping("/template/email")
    public EmailTemplate updateEmailTemplate(@AuthenticationPrincipal Jwt jwt,
            @RequestParam(value = "type") EmailTemplateType type,
            @RequestBody EmailTemplate emailTemplate) {
        return templateService.updateEmailTemplate(type, emailTemplate, getUserIdFromToken(jwt));
    }

    @GetMapping("/template/sms")
    public SmsTemplate getSmsTemplate(@RequestParam(value = "type") SmsTemplateType type) {
        return smsTemplateRepository.findLatest(type)
                .orElseThrow(() -> new RuntimeException("SMS template not found"));
    }
    @PutMapping("/template/sms")
    public SmsTemplate updateSmsTemplate(@AuthenticationPrincipal Jwt jwt,
            @RequestParam(value = "type") SmsTemplateType type,
                                      @RequestBody SmsTemplate smsTemplate) {
        return templateService.updateSmsTemplate(type, smsTemplate, getUserIdFromToken(jwt));
    }

    private Long getUserIdFromToken(Jwt jwt) {
        return jwt.getClaim(USER_ID_CLAIM);
    }
}
