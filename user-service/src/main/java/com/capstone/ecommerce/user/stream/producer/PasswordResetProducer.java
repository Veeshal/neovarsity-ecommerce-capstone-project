package com.capstone.ecommerce.user.stream.producer;

import com.capstone.ecommerce.user.entity.PasswordResetToken;
import com.capstone.ecommerce.user.stream.event.PasswordResetEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class PasswordResetProducer {

    @Value("${ecom.kafka.topics.user-password-reset}")
    private String passwordResetTopic;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendPasswordResetEvent(PasswordResetToken passwordResetToken) {

        Long userId = passwordResetToken.getUser().getId();
        String email = passwordResetToken.getUser().getEmail();
        String phone = passwordResetToken.getUser().getPhoneNumber();
        String token = passwordResetToken.getToken();

        log.debug("Sending password reset event for email: {}  with token: {}", email, token);

        PasswordResetEvent event = new PasswordResetEvent(
                userId, email, phone, token
        );

        kafkaTemplate.send(passwordResetTopic, event);
    }
}
