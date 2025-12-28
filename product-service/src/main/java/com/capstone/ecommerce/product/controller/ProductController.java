package com.capstone.ecommerce.product.controller;

import com.capstone.ecommerce.product.dto.ProductDto;
import com.capstone.ecommerce.product.dto.QuantityUpdateDto;
import com.capstone.ecommerce.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("v1/product")
public class ProductController {

    private final ProductService productService;


    // =========================== Product Buyer ===========================
    @GetMapping
    public List<ProductDto> searchProducts(@RequestParam(required = false, name = "search") String search) {
        return productService.searchByKeyword(search);
    }


    @GetMapping("{id}")
    public ProductDto getProduct(@PathVariable(name = "id") Long productId) {
        return productService.getProduct(productId);
    }


    // =========================== Product Owner ===========================

    @PostMapping
    public ProductDto createProduct(@RequestBody ProductDto product) {
        return productService.createProduct(product);
    }

    @PutMapping
    public ProductDto updateProduct(@RequestBody ProductDto product) {
        return productService.updateProduct(product);
    }

    @PutMapping("{id}/deactivate")
    public ProductDto deactivate(@PathVariable(name = "id") Long productId) {
        return productService.updateProductActiveState(productId, false);
    }

    @PutMapping("{id}/activate")
    public ProductDto activateProduct(@PathVariable(name = "id") Long productId) {
        return productService.updateProductActiveState(productId, true);
    }

    @PutMapping("/stock")
    public ProductDto updateProductStock(@RequestBody QuantityUpdateDto dto) {
        return productService.updateProductStock(dto.productId(), dto.quantity());
    }

    @DeleteMapping("{id}")
    public void deleteProduct(@PathVariable(name = "id") Long productId) {
        productService.deleteProduct(productId);
    }
}
