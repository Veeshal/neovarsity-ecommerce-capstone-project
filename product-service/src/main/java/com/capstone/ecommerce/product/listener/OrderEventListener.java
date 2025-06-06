package com.capstone.ecommerce.product.listener;

import com.capstone.ecommerce.product.repository.ProductRepository;
import com.capstone.ecommerce.product.dto.OrderCompletedEvent;
import com.capstone.ecommerce.product.dto.ProductDto;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class OrderEventListener {

    private final ProductRepository productRepository;

    @Autowired
    public OrderEventListener(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @KafkaListener(topics = "${kafka.topic.order-completed}", groupId = "product-service")
    @Transactional
    public void handleOrderCompleted(OrderCompletedEvent event) {
        for (ProductDto item : event.orderItems()) {
            Long productId = item.id();
            int quantity = item.quantity();
            productRepository.findById(productId).ifPresent(product -> {
                int newQty = product.getQuantity() - quantity;
                product.setQuantity(newQty);
//                product.setQuantity(Math.max(newQty, 0));
                productRepository.save(product);
            });
        }
    }
}
