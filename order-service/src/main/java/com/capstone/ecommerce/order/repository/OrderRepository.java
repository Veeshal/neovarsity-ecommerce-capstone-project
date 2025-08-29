package com.capstone.ecommerce.order.repository;

import com.capstone.ecommerce.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, String> {

    Page<Order> findByUserId(Long userId, Pageable pageable);

    Optional<Order> findByUserIdAndOrderId(Long userId, UUID orderId);

}