package com.capstone.ecommerce.notification.strategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;
import software.amazon.awssdk.services.sns.model.PublishRequest;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component("AWS_SMS_STRATEGY")
public class AwsSmsStrategy {

    private final SnsClient client;

    public void sendSMS(String to, String message) {

        var attributes = Map.of("AWS.SNS.SMS.SMSType",
                MessageAttributeValue.builder()
                        .dataType("String")
                        .stringValue("Transactional") // or Promotional
                        .build());

        var request = PublishRequest.builder()
                .phoneNumber(to)
                .message(message)
                .messageAttributes(attributes)
                .build();

        client.publish(request);

    }
}
