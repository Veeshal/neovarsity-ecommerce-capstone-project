package com.capstone.ecommerce.notification.service;

import com.capstone.ecommerce.notification.repository.EmailTemplateRepository;
import com.capstone.ecommerce.notification.strategy.MailStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.capstone.ecommerce.notification.service.MailTestUtil.createEmailTemplate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @InjectMocks private MailService mailService;
    @Mock private EmailTemplateRepository emailRepository;
    @Mock private MailStrategy mailStrategy;

    @Test
    void sendWelcomeEmail() {
    }

    @Test
    void sendPasswordResetEmail() {
    }

    @Test
    void sendOrderConfirmationEmail_Success() {
        String to = "to@abc.com";
        String orderId = "order123";

        String subjectTemplate = "Your Order {{orderId}} is Placed";
        String subject = "Your Order " + orderId + " is Placed";

        String bodyTemplate = """
                Dear {{firstName}},
                
                Thank you for your order {{orderId}} placed on {{orderDate}}. We are processing it and will notify you once it's shipped.
                
                Thank you for shopping with us!
                """;
        String body = """
                Dear Customer,
                
                Thank you for your order order123 placed on 2024-06-10. We are processing it and will notify you once it's shipped.
                
                Thank you for shopping with us!
                """;

        var emailTemplate = createEmailTemplate(subjectTemplate, bodyTemplate);
        given(emailRepository.findLatest(any())).willReturn(Optional.of(emailTemplate));

        mailService.sendOrderConfirmationEmail(to, orderId, "2024-06-10");


        verify(mailStrategy, times(1))
                .send(eq(to), eq(subject), eq(body));

    }

    @Test
    void sendMail() {
    }
}