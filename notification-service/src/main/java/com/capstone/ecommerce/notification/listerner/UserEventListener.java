package com.capstone.ecommerce.notification.listerner;

import com.capstone.ecommerce.notification.model.UserPasswordResetEvent;
import com.capstone.ecommerce.notification.model.UserRegisteredEvent;
import com.capstone.ecommerce.notification.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserEventListener {

    private final MailService mailService;

    @KafkaListener(topics = "${ecom.kafka.topics.user-registered}", containerFactory = "userKafkaListenerFactory")
    @Transactional
    public void handleUserRegistered(UserRegisteredEvent event) {
        mailService.sendWelcomeEmail(event.email(), event.username(), event.firstName(), event.lastName());
    }

    @KafkaListener(topics = "${ecom.kafka.topics.password-reset}", containerFactory = "userKafkaListenerFactory")
    @Transactional
    public void handleUserPasswordReset(UserPasswordResetEvent event) {
        mailService.sendPasswordResetEmail(event.email(), event.otp());
    }

}
