package com.capstone.ecommerce.product.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@Entity
public class Category extends BaseEntity {

    private String name;

    @OneToMany(mappedBy = "category")
    private List<Product> products;
}
