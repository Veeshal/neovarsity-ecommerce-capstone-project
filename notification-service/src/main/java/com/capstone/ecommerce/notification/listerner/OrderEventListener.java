package com.capstone.ecommerce.notification.listerner;

import com.capstone.ecommerce.notification.model.OrderPlacedEvent;
import com.capstone.ecommerce.notification.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderEventListener {

    private final MailService mailService;

    @KafkaListener(topics = "${ecom.kafka.topics.order-placed}", containerFactory = "orderKafkaListenerFactory")
    @Transactional
    public void handleOrderPlacedEvent(OrderPlacedEvent event) {
        log.info("order placed event received: {}", event.toString());
        mailService.sendOrderConfirmationEmail("vishalmoorthy15@gmail.com", event.orderId(), event.orderDate());
    }

}