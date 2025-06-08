package com.capstone.ecommerce.user.service;

import com.capstone.ecommerce.user.entity.AppUser;
import com.capstone.ecommerce.user.strategy.SocialLoginStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final JwtTokenService tokenService;
    private final AppUserService userService;
    private final PasswordEncoder passwordEncoder;

    // Map of provider name to strategy
    private final Map<String, SocialLoginStrategy> socialLoginStrategies;
    private final AppUserService appUserService;

    public String login(String username, String password) {
        AppUser user = (AppUser) userService.loadUserByUsername(username);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        return tokenService.generateToken(user);
    }

    @Transactional
    public String loginWithSocial(String provider, String token) {
        // TODO: Improve security.
        SocialLoginStrategy strategy = socialLoginStrategies.get(provider.toLowerCase());
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported social provider: " + provider);
        }
        AppUser newUser = strategy.login(token);
        var user = appUserService.registerUser(newUser);

        return tokenService.generateToken(user);
    }
}