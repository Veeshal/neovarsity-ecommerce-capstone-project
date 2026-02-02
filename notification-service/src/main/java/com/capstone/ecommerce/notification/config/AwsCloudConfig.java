package com.capstone.ecommerce.notification.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.sns.SnsClient;

@Configuration
public class AwsCloudConfig {

    @Value("${aws.region}")
    private String region;

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider() {
        return DefaultCredentialsProvider.create();
    }

    @Bean
    public SnsClient snsClient(AwsCredentialsProvider awsCredentialsProvider) {

        return SnsClient.builder()
                .region(Region.of(region))
                .credentialsProvider(awsCredentialsProvider)
                .build();
    }

    @Bean
    public SesClient sesClient(AwsCredentialsProvider awsCredentialsProvider) {

        return SesClient.builder()
                .region(Region.of(region))
                .credentialsProvider(awsCredentialsProvider)
                .build();
    }
}
