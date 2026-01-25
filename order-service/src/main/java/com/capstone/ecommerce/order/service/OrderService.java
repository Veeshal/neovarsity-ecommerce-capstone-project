package com.capstone.ecommerce.order.service;

import com.capstone.ecommerce.order.client.PaymentClient;
import com.capstone.ecommerce.order.client.ProductClient;
import com.capstone.ecommerce.order.dto.Cart;
import com.capstone.ecommerce.order.dto.CartItem;
import com.capstone.ecommerce.order.entity.*;
import com.capstone.ecommerce.order.exception.EmptyCartException;
import com.capstone.ecommerce.order.repository.OrderItemRepository;
import com.capstone.ecommerce.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentClient paymentClient;
    private final ProductClient productClient;
    @Value("${ecom.kafka.topics.order-placed}")
    private String orderPlacedTopic;

    private final KafkaTemplate<String, Object> kafkaTemplate;


    @Transactional
    public Order initiateOrder(Long userId, Long addressId, int paymentMethodId, Cart cart) {

        // Validate product availability
        productClient.validateAllProducts(cart);

        var payment = PaymentMethod.from(paymentMethodId);

        // Create order
        var order = createOrder(userId, addressId, payment, cart);
        var totalAmount = order.getTotalAmount();

        // Save order to the database
        order = orderRepository.save(order);

        var orderId = order.getOrderId();
        log.info("Order created with ID: {}", orderId);

        if (payment == PaymentMethod.CASH_ON_DELIVERY) {
            order.setStatus(OrderStatus.PLACED);
            notifyOrderPlaced(order);
            return order;
        }

        var paymentLink = paymentClient.generatePayment(order, payment, totalAmount);
        order.setPaymentLink(paymentLink.link());
        log.info("Payment link generated: {}", paymentLink);

        return order;
    }

    public Order createOrder(Long userId, Long addressId, PaymentMethod payment, Cart cart) {
        Order order = new Order();
        order.setStatus(OrderStatus.INITIATED);
        order.setUserId(userId);
        order.setDeliveryAddressId(addressId);
        order.setPaymentMethod(payment);

        BigDecimal totalAmount = BigDecimal.ZERO;

        if (cart.items() == null || cart.items().isEmpty()) {
            throw new EmptyCartException();
        }

        for (CartItem item : cart.items()) {

            var orderItem = new OrderItem();
            orderItem.setQuantity(item.quantity());
            orderItem.setPrice(BigDecimal.valueOf(item.product().price()));
            orderItem.setProductName(item.product().name());
            orderItem.setProductId(item.product().id());

            var qty = BigDecimal.valueOf(orderItem.getQuantity());
            var price = orderItem.getPrice();

            totalAmount = totalAmount.add(qty.multiply(price));

            order.addItem(orderItem);
        }

        order.setTotalAmount(totalAmount);

        return order;
    }

    @Transactional
    public void placeOrder(String orderId) {
        var order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        order.setStatus(OrderStatus.PLACED);
        orderRepository.save(order);
        notifyOrderPlaced(order);
    }

    public void notifyOrderPlaced(Order order) {
        kafkaTemplate.send(orderPlacedTopic, new OrderPlacedEvent(
                order.getOrderId().toString(),
                order.getUserId(),
                order.getTotalAmount()
        ));
    }


    public Order getOrder(Long userId, String orderId) {
        return orderRepository.findByUserIdAndOrderId(userId, UUID.fromString(orderId))
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }

    public Page<Order> getOrderHistory(Long userId, int page, int size) {
        var pageRequest = PageRequest.of(page, size)
                .withSort(Sort.by("updatedAt").descending());
        return orderRepository.findByUserId(userId, pageRequest);
    }

    public Page<OrderItem> getOrderItems(Long userId, String orderId, int page, int size) {
        var pageRequest = PageRequest.of(page, size);

        var order = getOrder(userId, orderId);
        return orderItemRepository.findByOrder(order, pageRequest);
    }
}
