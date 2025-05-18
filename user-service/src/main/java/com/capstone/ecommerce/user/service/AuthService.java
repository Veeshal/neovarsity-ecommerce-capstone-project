package com.capstone.ecommerce.user.service;

import com.capstone.ecommerce.user.entity.AppUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final JwtTokenService tokenService;
    private final AppUserService userService;

    private final PasswordEncoder passwordEncoder;

    public String login(String username, String password) {
        AppUser user = (AppUser) userService.loadUserByUsername(username);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        return tokenService.generateToken(user);
    }

    public String loginWithGoogle(String token) {
        AppUser user = userService.registerGoogleUser(token);
        return tokenService.generateToken(user);
    }

}
