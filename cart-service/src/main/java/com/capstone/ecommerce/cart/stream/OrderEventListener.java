package com.capstone.ecommerce.cart.stream;

import com.capstone.ecommerce.cart.dto.event.OrderPlacedEvent;
import com.capstone.ecommerce.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderEventListener {

    private final CartService cartService;

    @KafkaListener(topics = "${ecom.kafka.topics.order-placed}", groupId = "cart-service")
    public void handleOrderPlaced(OrderPlacedEvent event) {
        log.info("Received order placed event: {}", event);
        cartService.clearCart(event.getUserId());
    }
}
