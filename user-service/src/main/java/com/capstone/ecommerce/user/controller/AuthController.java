package com.capstone.ecommerce.user.controller;

import com.capstone.ecommerce.user.dto.*;
import com.capstone.ecommerce.user.service.AppUserService;
import com.capstone.ecommerce.user.service.AuthService;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("auth")
public class AuthController {

    private final AuthService authService;
    private final AppUserService userService;
    private final JWKSource<SecurityContext> jwtSource;

    @GetMapping(".well-known/jwks.json")
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

    @PostMapping("register")
    public AppUserDto register(@RequestBody final RegisterRequest request) {
        var user = userService.registerUser(request.email(), request.password(), request.name());
        return new AppUserDto(user.getUsername(), user.getEmail());
    }

    @PostMapping("social/login")
    public LoginResponse registerWithSocial(@RequestBody final SocialLoginRequest request) {
        String token = authService.loginWithSocial(request.provider(), request.token());
        return new LoginResponse(token);
    }

    @PostMapping("login")
    public LoginResponse login(@RequestBody final LoginRequest request) throws Exception {

        String token = authService.login(request.username(), request.password());

        return new LoginResponse(token);
    }



}