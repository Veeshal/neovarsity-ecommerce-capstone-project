package com.capstone.ecommerce.product.service;

import com.capstone.ecommerce.product.dto.ValidateCartItem;
import com.capstone.ecommerce.product.dto.ProductDto;
import com.capstone.ecommerce.product.exceptions.CategoryNotFoundException;
import com.capstone.ecommerce.product.exceptions.ProductNotFoundException;
import com.capstone.ecommerce.product.exceptions.ProductOutOfStockException;
import com.capstone.ecommerce.product.repository.ProductRepository;
import com.capstone.ecommerce.product.repository.CategoryRepository;
import com.capstone.ecommerce.product.entity.Product;
import com.capstone.ecommerce.product.entity.Category;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Service
public class ProductService {

    private final ElasticSearchService searchService;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public List<ProductDto> searchByKeyword(String keyword) {
        // Return all products (from DB or ES)

        log.info("Searching products with keyword: {}", keyword);

        try {
            return searchService.searchByKeyword(keyword);

        } catch (IOException e) {
            // Handle exception as needed
            return List.of();
        }
    }

    @Transactional(readOnly = true)
    public ProductDto getProduct(Long productId) {
        var product = productRepository.findProductById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        return ProductDto.fromEntity(product);
    }

    @Transactional
    public ProductDto createProduct(ProductDto productDto) {
        Product product = productDto.toEntity();

        // Set createdAt and updatedAt here
        LocalDateTime now = LocalDateTime.now();
        product.setCreatedAt(now);
        product.setUpdatedAt(now);

        // Fetch and set managed Category entity
        Category category;
        if (productDto.categoryId() != null) {
            category = categoryRepository.findById(product.getCategory().getId())
                .orElseThrow(() -> new CategoryNotFoundException(productDto.categoryId()));
        } else if (Strings.isNotBlank(productDto.categoryName())) {
            category = categoryRepository.findByName(productDto.categoryName())
                    .orElseGet(() -> {
                        Category newCategory = new Category();
                        newCategory.setName(productDto.categoryName());
                        return categoryRepository.save(newCategory);
                    });
        } else {
            throw new IllegalArgumentException("Category must be provided");
        }

        product.setCategory(category);

        var savedProduct = productRepository.save(product);
        ProductDto savedDto = ProductDto.fromEntity(savedProduct);

        searchService.indexProduct(savedDto);

        return savedDto;
    }

    @Transactional
    public ProductDto updateProduct(ProductDto productDto) {
        Long productId = productDto.id();
        var product = productRepository.findById(productId).orElseThrow();
        product.updateFromDto(productDto);

        if (productDto.categoryId() != null) {
            categoryRepository.findById(productDto.categoryId()).ifPresent(product::setCategory);
        }
        var updatedDto = ProductDto.fromEntity(productRepository.save(product));
        searchService.updateProductIndex(updatedDto);
        return updatedDto;
    }

    @Transactional
    public ProductDto updateProductActiveState(Long productId, boolean activate) {
        var product = productRepository.findById(productId)
                        .orElseThrow(() -> new ProductNotFoundException(productId));

        if (activate)  {
            log.info("Activating product with ID: {}", productId);
            product.activate();
        } else {
            log.info("Deactivating product with ID: {}", productId);
            product.deactivate();
        }
        var dto = ProductDto.fromEntity(productRepository.save(product));

        searchService.updateProductIndex(dto);

        return dto;
    }

    @Transactional
    public void deleteProduct(Long productId) {
        var product = productRepository.findById(productId)
                        .orElseThrow(() -> new ProductNotFoundException(productId));
        productRepository.delete(product);
        searchService.deleteProductFromIndex(productId);
    }


    @Transactional
    public ProductDto updateProductStock(Long productId, int newStock) {
        var product = productRepository.findById(productId)
                        .orElseThrow(() -> new ProductNotFoundException(productId));

        if (product.getQuantity() + newStock < 0) {
            throw new ProductOutOfStockException(productId);
        }

        product.updateQuantity(newStock);
        var dto = ProductDto.fromEntity(productRepository.save(product));
        searchService.updateProductIndex(dto);

        return dto;
    }

    public void validateProductStock(List<ValidateCartItem> cartItems) {

        List<Long> productIds = cartItems.stream().map(ValidateCartItem::productId).toList();
        productRepository.findAllById(productIds).forEach(product -> {
            var requestedQty = cartItems.stream()
                    .filter(item -> item.productId().equals(product.getId()))
                    .map(ValidateCartItem::quantity)
                    .findFirst()
                    .orElse(0);
            if (product.getQuantity() < requestedQty) {
                throw new ProductOutOfStockException(product.getId());
            }
        });
    }
}
