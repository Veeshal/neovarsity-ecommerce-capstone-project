package com.capstone.ecommerce.product.dto;

import com.capstone.ecommerce.product.entity.Product;
import com.capstone.ecommerce.product.entity.ProductImages;
import com.capstone.ecommerce.product.entity.Category;
import com.capstone.ecommerce.product.entity.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

public record ProductDto(
        Long id,
        String name,
        String description,
        String thumbnail,
        List<String> images,
        BigDecimal price,
        int quantity,
        Long categoryId,
        String categoryName,
        List<SpecificationDto> specifications,
        boolean isActive,
        String createdAt,
        String updatedAt
) {
    public static ProductDto fromEntity(Product product) {
        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getImages().stream().findFirst().map(ProductImages::getImageUrl).orElse(null),
                product.getImages().stream().map(ProductImages::getImageUrl).toList(),
                product.getPrice(),
                product.getQuantity(),
                product.getCategory() != null ? product.getCategory().getId() : null,
                product.getCategory() != null ? product.getCategory().getName() : null,
                product.getSpecifications().stream()
                        .map(spec -> new SpecificationDto(spec.getName(), spec.getValue()))
                        .toList(),
                product.isActive(),
                product.getCreatedAt().toString(),
                product.getUpdatedAt() != null ? product.getUpdatedAt().toString() : null
        );
    }

    public Product toEntity() {
        Product product = new Product();
        product.setId(this.id);
        product.setName(this.name);
        product.setDescription(this.description);
        product.setPrice(this.price);
        product.setQuantity(this.quantity);
        product.setActive(this.isActive);

        // Only set category ID, let service layer fetch managed Category entity
        if (this.categoryId != null) {
            Category category = new Category();
            category.setId(this.categoryId);
            product.setCategory(category);
        }

        // Set specifications
        if (this.specifications != null) {
            product.setSpecifications(
                this.specifications.stream()
                    .map(dto -> {
                        Specification spec = new Specification();
                        spec.setName(dto.name());
                        spec.setValue(dto.value());
                        spec.setProduct(product);
                        return spec;
                    })
                    .collect(Collectors.toList())
            );
        } else {
            product.setSpecifications(new ArrayList<>());
        }

        // Set images
        if (this.images != null) {
            product.setImages(
                this.images.stream()
                    .map(url -> {
                        ProductImages img = new ProductImages();
                        img.setImageUrl(url);
                        img.setProduct(product);
                        return img;
                    })
                    .collect(Collectors.toList())
            );
        } else {
            product.setImages(new ArrayList<>());
        }

        // Set createdAt and updatedAt if present
        if (this.createdAt != null) {
            product.setCreatedAt(LocalDateTime.parse(this.createdAt));
        }
        if (this.updatedAt != null) {
            product.setUpdatedAt(LocalDateTime.parse(this.updatedAt));
        }

        return product;
    }
}
