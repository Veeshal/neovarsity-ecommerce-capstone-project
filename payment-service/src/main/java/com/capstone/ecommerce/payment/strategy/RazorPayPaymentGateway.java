package com.capstone.ecommerce.payment.strategy;

import com.capstone.ecommerce.payment.dto.Item;
import com.capstone.ecommerce.payment.dto.PaymentLinkInfo;
import com.capstone.ecommerce.payment.exceptions.PaymentLinkGenerationException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component(PaymentGatewayStrategy.PAYMENT_GATEWAY_RAZORPAY)
public class RazorPayPaymentGateway implements PaymentGatewayStrategy {

    @Override
    public PaymentLinkInfo createPaymentLink(String orderId, List<Item> items, String currency) throws PaymentLinkGenerationException {
        return null;
    }

    @Override
    public void handleWebhook(String payload, String signature) {
        // TODO: Handle Razorpay webhook
    }
}
