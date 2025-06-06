package com.capstone.ecommerce.product.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {

    @GetMapping
    public String sample() {
        return "Hello from Product Service!";
    }
}
