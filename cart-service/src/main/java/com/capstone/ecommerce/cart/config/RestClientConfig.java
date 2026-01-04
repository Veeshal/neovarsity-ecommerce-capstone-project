package com.capstone.ecommerce.cart.config;

import jakarta.servlet.http.HttpServletRequest;import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;import org.springframework.http.client.ClientHttpRequestInterceptor;import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;import org.springframework.web.context.request.RequestContextHolder;import org.springframework.web.context.request.ServletRequestAttributes;


@Slf4j
@RequiredArgsConstructor
@Configuration
public class RestClientConfig {

    @Value("${ecom.client.product-service.url}")
    private String productServiceBaseUrl;

    @Value("${ecom.client.user-service.url}")
    private String userServiceBaseUrl;

    @Value("${ecom.client.order-service.url}")
    private String orderServiceBaseUrl;

    @Bean
    public RestClient productRestClient() {
        return RestClient.builder()
                .baseUrl(productServiceBaseUrl) // Base URL for Product Service
                .requestInterceptor(authInterceptor())
                .build();
    }

    @Bean
    public RestClient userRestClient() {
        return RestClient.builder()
                .baseUrl(userServiceBaseUrl) // Base URL for User Service
                .requestInterceptor(authInterceptor())
                .build();
    }

    @Bean
    public RestClient orderRestClient() {
        return RestClient.builder()
                .baseUrl(orderServiceBaseUrl) // Base URL for Order Service
                .requestInterceptor(authInterceptor())
                .build();
    }

    public ClientHttpRequestInterceptor authInterceptor() {
        return (request, body, execution) -> {
            // Add authentication headers or tokens here
            log.info("Adding authentication to request: {}", request.getURI());

            // Grab current request
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attrs != null) {
                HttpServletRequest currentRequest = attrs.getRequest();

                // Copy auth header from incoming request
                var headerValue = currentRequest.getHeaders(HttpHeaders.AUTHORIZATION);
                request.getHeaders().add(HttpHeaders.AUTHORIZATION, headerValue.nextElement());
            }

            return execution.execute(request, body);
        };
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder()
                .build();
    }
}
