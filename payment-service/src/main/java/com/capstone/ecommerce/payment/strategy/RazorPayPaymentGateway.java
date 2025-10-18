package com.capstone.ecommerce.payment.strategy;

import com.capstone.ecommerce.payment.exceptions.PaymentLinkGenerationException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component(PaymentGatewayStrategy.PAYMENT_GATEWAY_RAZORPAY)
public class RazorPayPaymentGateway implements PaymentGatewayStrategy {

    @Override
    public String createPaymentLink(String paymentDetails) throws PaymentLinkGenerationException {
        return "";
    }

    @Override
    public void handleWebhook(HttpServletRequest request) {
        // TODO: Handle Razorpay webhook
    }
}
