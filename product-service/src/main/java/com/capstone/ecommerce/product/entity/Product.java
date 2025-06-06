package com.capstone.ecommerce.product.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Product extends BaseEntity {

    private String name;
    private String description;

    @ManyToOne
    private Category category;

    @OneToMany(mappedBy = "product")
    private List<Specification> specifications;

    @OneToMany(mappedBy = "product")
    private List<ProductImages> images;

    private BigDecimal price;
    private int quantity;

    private boolean isActive;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

}
