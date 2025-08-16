package com.capstone.ecommerce.user.controller;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(".well-known/jwks.json")
public class JwtController {

    private final JWKSource<SecurityContext> jwtSource;

    @GetMapping
    public Map<String, Object> getJwks() {
        try {
            // Extract the JWKSet from the JWKSource
            var immutableJwkSet = (ImmutableJWKSet<SecurityContext>) jwtSource;
            JWKSet jwkSet = immutableJwkSet.getJWKSet();
            // Only expose public keys
            var publicJwkSet = jwkSet.toPublicJWKSet();
            return publicJwkSet.toJSONObject();
        } catch (Exception e) {
            throw new RuntimeException("Failed to expose JWKS", e);
        }
    }
}
