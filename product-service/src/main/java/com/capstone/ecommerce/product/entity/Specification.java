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
public class Specification extends BaseEntity {

    private String name;
    private String value;

    @ManyToOne
    private Product product;
}
