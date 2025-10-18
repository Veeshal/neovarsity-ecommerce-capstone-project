package com.capstone.ecommerce.payment.config;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RazorPayConfig {

    @Value("${ecom.razorpay.key}")
    private String RAZORPAY_KEY;

    @Value("${ecom.razorpay.secret_key}")
    private String RAZORPAY_SECRET;

    @Bean
    public RazorpayClient razorpayClient() throws RazorpayException {
        return new RazorpayClient(RAZORPAY_KEY, RAZORPAY_SECRET);
    }
}
