package com.capstone.ecommerce.product.listener;

import com.capstone.ecommerce.product.entity.Product;
import com.capstone.ecommerce.product.repository.ProductRepository;
import com.capstone.ecommerce.product.dto.OrderCompletedEvent;
import com.capstone.ecommerce.product.dto.ProductDto;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Collectors;

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

        var productIds = event.orderItems().stream().map(ProductDto::id).toList();

        var products = productRepository.findAllById(productIds);
        var productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, product -> product));

        boolean isStockSufficient = event.orderItems().stream().allMatch(item -> {
            Product product = productMap.get(item.id());
            return product != null && product.getQuantity() >= item.quantity();
        });

        if (!isStockSufficient) {
            throw new RuntimeException("Insufficient stock for one or more products in the order");
        }

        // Deduct stock quantities
        event.orderItems().forEach(item -> {
            Product product = productMap.get(item.id());
            if (product != null) {
                product.setQuantity(product.getQuantity() - item.quantity());
                productRepository.save(product);
            }
        });

    }
}
