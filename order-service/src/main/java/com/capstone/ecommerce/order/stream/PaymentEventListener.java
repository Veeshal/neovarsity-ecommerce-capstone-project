package com.capstone.ecommerce.order.stream;

import com.capstone.ecommerce.order.entity.OrderPlacedEvent;
import com.capstone.ecommerce.order.entity.PaymentEvent;
import com.capstone.ecommerce.order.entity.PaymentEventStatus;
import com.capstone.ecommerce.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentEventListener {

    private final OrderService orderService;

    @KafkaListener(topics = "${ecom.kafka.topics.payment}", groupId = "order-service")
    public void handleOrderPlaced(PaymentEvent event) {
        log.info("Received payment processed event: {}", event);
        if (event.status() == PaymentEventStatus.PAYMENT_LINK_PAID) {
            log.info("Payment completed for order id: {}", event.orderId());
            orderService.placeOrder(event.orderId());
        }
    }
}
