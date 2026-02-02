package com.capstone.ecommerce.notification.strategy;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

@Slf4j
@RequiredArgsConstructor
@Component("AWS_MAIL_STRATEGY")
public class AwsMailStrategy implements MailStrategy {

    @Value("${ecom.email.sender}")
    private String senderEmail;

    private final SesClient client;

    @Override
    public void send(String to, String subject, String body) {
        log.info("Sending email via AWS SES to: {}, subject: {}, body: {}", to, subject, body);

        Destination destination = Destination.builder()
                .toAddresses(to)
                .build();

        Content subjectContent = Content.builder()
                .data(subject)
                .charset("UTF-8")
                .build();

        Content bodyContent = Content.builder()
                .data(body)
                .charset("UTF-8")
                .build();

        Body emailBody = Body.builder()
                .text(bodyContent) // or .html()
                .build();

        Message message = Message.builder()
                .subject(subjectContent)
                .body(emailBody)
                .build();

        SendEmailRequest request = SendEmailRequest.builder()
                .destination(destination)
                .message(message)
                .source(senderEmail)
                .build();

        client.sendEmail(request);

    }
}
