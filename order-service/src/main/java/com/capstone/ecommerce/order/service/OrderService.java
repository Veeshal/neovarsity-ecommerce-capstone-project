package com.capstone.ecommerce.order.service;

import com.capstone.ecommerce.order.client.PaymentClient;
import com.capstone.ecommerce.order.client.ProductClient;
import com.capstone.ecommerce.order.dto.Cart;
import com.capstone.ecommerce.order.dto.CartItem;
import com.capstone.ecommerce.order.entity.Order;
import com.capstone.ecommerce.order.entity.OrderItem;
import com.capstone.ecommerce.order.entity.OrderStatus;
import com.capstone.ecommerce.order.entity.PaymentMethod;
import com.capstone.ecommerce.order.repository.OrderItemRepository;
import com.capstone.ecommerce.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentClient paymentClient;
    private final ProductClient productClient;

    @Transactional
    public Order placeOrder(Long userId, Long addressId, String paymentMethod, Cart cart) {

        // Validate product availability
        productClient.validateAllProducts(cart);

        var payment = PaymentMethod.from(paymentMethod);


        // Create order and order items
        Order order = new Order();
        order.setStatus(OrderStatus.INITIATED);
        order.setUserId(userId);
        order.setDeliveryAddressId(addressId);
        order.setPaymentMethod(payment);

        BigDecimal totalAmount = BigDecimal.ZERO;

        if (cart.items() == null || cart.items().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty. Cannot place order.");
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

        if (totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Total amount must be greater than zero.");
        }

        order.setTotalAmount(totalAmount);

        // Save order to the database
        orderRepository.save(order);


        var orderId = order.getOrderId();
        log.info("Order created with ID: {}", orderId);

        if (payment != PaymentMethod.CASH_ON_DELIVERY) {
            var paymentLink = paymentClient.generatePayment(userId, orderId, paymentMethod, totalAmount);
            order.setPaymentLink(paymentLink);
            log.info("Payment link generated: {}", paymentLink);
        } else {
            order.setStatus(OrderStatus.PLACED);

        }

        return order;
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
