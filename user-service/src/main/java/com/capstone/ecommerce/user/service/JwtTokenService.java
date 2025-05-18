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

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    public String generateToken(AppUser user) {

        var currentTime = Instant.now();


        // Create JWT claims
        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuer("http://localhost:8080")
                .subject(user.getUsername())
                .issuedAt(currentTime)
                .expiresAt(currentTime.plus(tokenDuration, ChronoUnit.SECONDS))
                .claim("role", "USER")
                .claim("sample", "value")
                .build();


        return jwtEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();

    }

    public String extractUsername(String token) {
        var jwt = jwtDecoder.decode(token);
        return jwt.getSubject();
    }
}
