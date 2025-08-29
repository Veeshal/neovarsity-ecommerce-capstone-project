package com.capstone.ecommerce.order.repository;

import com.capstone.ecommerce.order.entity.Order;
import com.capstone.ecommerce.order.entity.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    Page<OrderItem> findByOrder(Order order, Pageable pageable);
}