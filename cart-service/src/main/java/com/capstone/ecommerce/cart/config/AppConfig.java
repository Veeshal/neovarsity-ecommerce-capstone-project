package com.capstone.ecommerce.cart.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@Configuration
public class AppConfig {

    private final JwtDecoder jwtDecoder;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(registry -> registry
                        .requestMatchers(openEndpointPatterns()).permitAll()
                        .anyRequest().authenticated()
                );

        http.oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwtConfigurer -> jwtConfigurer.decoder(jwtDecoder)));

        return http.build();
    }

    private String[] openEndpointPatterns() {
        return new String[]{
                "/actuator/**",
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/webjars/**",
                "/v3/api-docs/**",
                "/error"
        };
    }
}
