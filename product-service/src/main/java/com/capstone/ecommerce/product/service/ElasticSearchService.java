package com.capstone.ecommerce.product.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.capstone.ecommerce.product.dto.ProductDto;
import com.capstone.ecommerce.product.exceptions.ProductElasticSearchSaveException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class ElasticSearchService {

    private static final String PRODUCT_INDEX = "products";

    private final ElasticsearchClient elasticsearchClient;

    public List<ProductDto> searchByKeyword(String keyword) throws IOException {

        var matchQuery = Query.of(q -> q.match(m -> m
                .field("name").query(keyword)
                .fuzziness("AUTO")));

        var activeFilter = Query.of(q -> q.term(m -> m
                .field("isActive").value(true)));

        SearchRequest request = SearchRequest.of(s -> s
                .index(PRODUCT_INDEX)
                .query(q -> q.bool(b -> b.must(matchQuery).filter(activeFilter))));

        SearchResponse<ProductDto> response = elasticsearchClient.search(request, ProductDto.class);
        return response.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());

    }

    public void indexProduct(ProductDto productDto) {
        try {
            elasticsearchClient.index(i -> i
                    .index(PRODUCT_INDEX)
                    .id(productDto.id().toString())
                    .document(productDto)
            );
            log.info("Indexed product with ID: {}", productDto.id());
        } catch (IOException e) {
            log.error("Failed to index product with ID: {}", productDto.id(), e);
            throw new ProductElasticSearchSaveException(e.getMessage());
        }
    }

    public void deleteProductFromIndex(Long productId) {
        try {
            elasticsearchClient.delete(d -> d
                    .index(PRODUCT_INDEX)
                    .id(productId.toString())
            );
            log.info("Deleted product from index with ID: {}", productId);
        } catch (IOException e) {
            log.error("Failed to delete product from index with ID: {}", productId, e);
            // Handle exception as needed
        }
    }

    public void updateProductIndex(ProductDto productDto) {
        indexProduct(productDto);
    }
}
