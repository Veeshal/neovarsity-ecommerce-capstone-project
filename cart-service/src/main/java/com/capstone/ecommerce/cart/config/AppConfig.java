package com.capstone.ecommerce.cart.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // TODO: Configure security JWT authentication
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
//                                .requestMatchers(
//                                        "/swagger-ui.html",
//                                        "/swagger-ui/**",
//                                        "/webjars/**",
//                                        "/v3/api-docs/**",
//                                        "/actuator/**"
//                                ).permitAll()
                                .anyRequest().permitAll());
        return http.build();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder()
                .build();
    }
}
