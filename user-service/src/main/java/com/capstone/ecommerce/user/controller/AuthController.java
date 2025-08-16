package com.capstone.ecommerce.user.controller;

import com.capstone.ecommerce.user.dto.*;
import com.capstone.ecommerce.user.service.AppUserService;
import com.capstone.ecommerce.user.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("v1/auth")
@Validated
public class AuthController {

    private final AuthService authService;
    private final AppUserService userService;

    @PostMapping("register")
    public AppUserDto register(@RequestBody final RegisterRequest request) {
        var user = userService.registerUser(request.email(), request.password(), request.name());
        return AppUserDto.from(user);
    }

    @PostMapping("social/login")
    public LoginResponse registerWithSocial(@Valid @RequestBody final SocialLoginRequest request) {
        String token = authService.loginWithSocial(request.provider(), request.token());
        return new LoginResponse(token, null);
    }

    @PostMapping("login")
    public LoginResponse login(@Valid @RequestBody final LoginRequest request) {
        log.info("Login request: {}", request);

        String token = authService.login(request.email(), request.password());

        return new LoginResponse(token, null);
    }



}