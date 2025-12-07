package com.capstone.ecommerce.user.service;

import com.capstone.ecommerce.user.entity.AppUser;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class JwtTokenService {

    @Value("${ecom.token_duration}")
    private long tokenDuration;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuer;

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    public String generateToken(AppUser user) {

        var currentTime = Instant.now();


        // Create JWT claims
        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuer(issuer)
                .subject(user.getName())
                .issuedAt(currentTime)
                .expiresAt(currentTime.plus(tokenDuration, ChronoUnit.SECONDS))
                .claim("role", "USER")
                .claim("email", user.getEmail())
                .claim("name", user.getName())
                .claim("userId", user.getId()) // TODO: Remove this in production. Find alternative to get userId from token
                .build();


        return jwtEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();

    }

    public String extractUsername(String token) {
        var jwt = jwtDecoder.decode(token);
        return jwt.getSubject();
    }
}
