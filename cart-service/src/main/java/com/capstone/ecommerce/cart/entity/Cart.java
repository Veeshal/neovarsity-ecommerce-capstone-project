package com.capstone.ecommerce.cart.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@Document(collection = "carts")
public class Cart {
    @Id
    private String id;
    private List<CartItem> items;
    private Long userId;
}
