package com.capstone.ecommerce.product.service;

import com.capstone.ecommerce.product.dto.ProductDto;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.capstone.ecommerce.product.exceptions.ProductElasticSearchSaveException;
import com.capstone.ecommerce.product.exceptions.ProductNotFoundException;
import com.capstone.ecommerce.product.repository.ProductRepository;
import com.capstone.ecommerce.product.repository.CategoryRepository;
import com.capstone.ecommerce.product.entity.Product;
import com.capstone.ecommerce.product.entity.Category;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;



@Slf4j
@RequiredArgsConstructor
@Service
public class ProductService {

    private final ElasticsearchClient elasticsearchClient;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public List<ProductDto> searchByKeyword(String keyword) {
        // Return all products (from DB or ES)

        log.info("Searching products with keyword: {}", keyword);

        try {
            Query matchQuery = Query.of(q -> q
                .match(m -> m
                    .field("name")
                    .query(keyword)
                    .fuzziness("AUTO")
                )
            );

            SearchRequest request = SearchRequest.of(s -> s
                .index("products")
                .query(matchQuery)
            );

            SearchResponse<ProductDto> response = elasticsearchClient.search(request, ProductDto.class);
            return response.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            // Handle exception as needed
            return List.of();
        }
    }

    public ProductDto getProduct(Long productId) {
        var product = productRepository.findById(productId)
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
        if (productDto.categoryId() != null) {
            Category category = categoryRepository.findById(product.getCategory().getId())
                .orElseGet(() -> {
                    Category newCategory = new Category();
                    newCategory.setName(product.getCategory().getName());
                    return categoryRepository.save(newCategory);
                });
            product.setCategory(category);
        } else if (productDto.categoryName() != null) {
            Category category = categoryRepository.findByName(productDto.categoryName())
                    .orElseGet(() -> {
                        Category newCategory = new Category();
                        newCategory.setName(productDto.categoryName());
                        return categoryRepository.save(newCategory);
                    });
            product.setCategory(category);
        } else {
            throw new IllegalArgumentException("Category must be provided");
        }

        var savedProduct = productRepository.save(product);
        ProductDto savedDto = ProductDto.fromEntity(savedProduct);

        // Insert into Elasticsearch
        try {
            elasticsearchClient.index(i -> i
                .index("products")
                .id(savedProduct.getId().toString())
                .document(savedDto)
            );
        } catch (IOException e) {
            // Handle exception as needed (log, rethrow, etc.)
            throw new ProductElasticSearchSaveException(e.getMessage());
        }

        return savedDto;
    }

    @Transactional
    public ProductDto updateProduct(ProductDto productDto) {
        Long productId = productDto.id();
        // TODO: Update product in the database

        return null;
    }

    @Transactional
    public void deactivateProduct(Long productId) {
        productRepository.findById(productId).ifPresent(product -> {
            product.setActive(false);
            product.setDeletedAt(LocalDateTime.now());
            productRepository.save(product);

            // Remove from Elasticsearch
            try {
                elasticsearchClient.delete(d -> d
                    .index("products")
                    .id(productId.toString())
                );
            } catch (IOException e) {
                // Handle exception as needed (log, rethrow, etc.)
                log.error("Failed to delete product from Elasticsearch: {}", e.getMessage());
            }
        });
    }
}
