package com.capstone.ecommerce.product.repository;

import com.capstone.ecommerce.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("""
            SELECT p
            FROM Product p
                LEFT JOIN FETCH p.category
                LEFT JOIN FETCH p.images
                LEFT JOIN FETCH p.specifications
            WHERE p.id = :id
        """)
    Optional<Product> findProductById(Long id);
}