package com.capstone.ecommerce.product.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@Entity
public class ProductImages extends BaseEntity {

    private String imageUrl;

    @ManyToOne
    private Product product;
}
