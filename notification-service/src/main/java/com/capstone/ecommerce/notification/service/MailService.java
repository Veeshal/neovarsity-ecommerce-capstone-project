package com.capstone.ecommerce.notification.service;

import com.capstone.ecommerce.notification.entity.EmailTemplateType;
import com.capstone.ecommerce.notification.model.TemplateKey;
import com.capstone.ecommerce.notification.repository.EmailTemplateRepository;
import com.capstone.ecommerce.notification.strategy.MailStrategy;
import com.samskivert.mustache.Mustache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.capstone.ecommerce.notification.model.TemplateKey.*;

@Slf4j
@Service
public class MailService {


    private final EmailTemplateRepository emailRepository;
    private final MailStrategy mailStrategy;

    public MailService(EmailTemplateRepository emailRepository, MailStrategy mailStrategy) {
        this.emailRepository = emailRepository;
        this.mailStrategy = mailStrategy;
    }

    private List<TemplateKey> getTemplateKeys(EmailTemplateType type) {

        return switch (type) {
            case WELCOME -> List.of(TemplateKey.USER_NAME, TemplateKey.FIRST_NAME, TemplateKey.LAST_NAME);
            case PASSWORD_RESET -> List.of(TemplateKey.USER_NAME, TemplateKey.PASSWORD_RESET_CODE);
            case ORDER_CONFIRMATION -> List.of(TemplateKey.ORDER_ID, TemplateKey.ORDER_DATE, TemplateKey.FIRST_NAME);
        };
    }

    public void sendWelcomeEmail(String to, String userName, String firstName, String lastName) {
        var templateOpt = emailRepository.findLatest(EmailTemplateType.WELCOME);
        if (templateOpt.isEmpty()) {
            log.error("WELCOME_EMAIL template not found");
            return;
        }

        var template = templateOpt.get();
        var compiler = Mustache.compiler();
        var variables = Map.of(
                USER_NAME.getKey(), userName,
                FIRST_NAME.getKey(), firstName,
                LAST_NAME.getKey(), lastName
        );

        var subject = compiler.compile(template.getSubject()).execute(variables);
        var body = compiler.compile(template.getBody()).execute(variables);


        sendMail(to, subject, body);
    }

    public void sendPasswordResetEmail(String to, String otp) {
        var templateOpt = emailRepository.findLatest(EmailTemplateType.PASSWORD_RESET);
        if (templateOpt.isEmpty()) {
            log.error("PASSWORD_RESET template not found");
            return;
        }

        var compiler = Mustache.compiler();
        var variables = Map.of(
                PASSWORD_RESET_CODE.getKey(), otp,
                USER_NAME.getKey(), "User" // TODO: Replace with actual user name if available
        );

        var template = templateOpt.get();
        var subject = compiler.compile(template.getSubject()).execute(variables);
        var body = compiler.compile(template.getBody()).execute(variables);

        sendMail(to, subject, body);
    }

    public void sendOrderConfirmationEmail(String to, String orderId, String orderDate) {
        var templateOpt = emailRepository.findLatest(EmailTemplateType.ORDER_CONFIRMATION);
        if (templateOpt.isEmpty()) {
            log.error("ORDER_CONFIRMATION template not found");
            return;
        }

        var template = templateOpt.get();

        var compiler = Mustache.compiler();
        var variables = Map.of(
                ORDER_ID.getKey(), orderId,
                FIRST_NAME.getKey(), "Customer", // TODO: Replace with actual customer name if available
                ORDER_DATE.getKey(), orderDate
        );

        // Subject
        var subject = compiler.compile(template.getSubject()).execute(variables);

        // Body
        var body = compiler.compile(template.getBody()).execute(variables);

        sendMail(to, subject, body);
    }

    public void sendMail(String to, String subject, String body) {
        log.info("Sending mail to: {}, subject: {}", to, subject);
        mailStrategy.send(to, subject, body);
    }
}
