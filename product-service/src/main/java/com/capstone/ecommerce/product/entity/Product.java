package com.capstone.ecommerce.product.entity;

import com.capstone.ecommerce.product.dto.ProductDto;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Product extends BaseEntity {

    private String name;
    private String description;

    @ManyToOne
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Specification> specifications = new HashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductImages> images = new HashSet<>();

    private BigDecimal price;
    private int quantity;

    private boolean isActive;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public void updateFromDto(ProductDto dto) {
        boolean isUpdated = false;

        if (Objects.nonNull(dto.name())) {
            this.name = dto.name();
            isUpdated = true;
        }

        if (Objects.nonNull(dto.description())) {
            this.description = dto.description();
            isUpdated = true;
        }

        // Note: Category, specifications, and images should be handled separately
        if (Objects.nonNull(dto.price())) {
            this.price = dto.price();
            isUpdated = true;
        }

        if (Objects.nonNull(dto.quantity())) {
            this.quantity = dto.quantity();
            isUpdated = true;
        }

        if (Objects.nonNull(dto.isActive())) {
            this.isActive = dto.isActive();
            isUpdated = true;
        }

        if (isUpdated) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void deactivate() {
        this.isActive = false;
        this.deletedAt = LocalDateTime.now();
    }

    public void activate() {
        this.isActive = true;
        this.deletedAt = null;
    }

    public void updateQuantity(int newQuantity) {
        this.quantity = quantity + newQuantity;
        this.updatedAt = LocalDateTime.now();
    }
}
