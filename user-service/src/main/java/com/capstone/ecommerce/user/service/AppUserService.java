package com.capstone.ecommerce.user.service;

import com.capstone.ecommerce.user.entity.AppUser;
import com.capstone.ecommerce.user.entity.SocialLogin;
import com.capstone.ecommerce.user.repository.AppUserRepository;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AppUserService implements UserDetailsService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return appUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    public Optional<AppUser> getUserByEmail(String email) {
        return appUserRepository.findByEmail(email);
    }

    public AppUser registerUser(String email, String password, String name) {
        var user = getUserByEmail(email);

        if (user.isPresent()) {
            throw new IllegalArgumentException("User already exists with email: " + email);
        }

        var newUser = new AppUser();
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setName(name);
        appUserRepository.save(newUser);
        return newUser;
    }

    @Transactional
    public AppUser registerUser(AppUser newUser) {
        var userOpt = appUserRepository.findByEmail(newUser.getEmail());

        if (userOpt.isPresent()) {
            AppUser existingUser = userOpt.get();
            // Only allow login if the social provider matches
            if (existingUser.getSocialLogin() != null && existingUser.getSocialLogin().equals(newUser.getSocialLogin())) {
                // Optionally update name if changed
                if (!existingUser.getName().equals(newUser.getName())) {
                    existingUser.setName(newUser.getName());
                    appUserRepository.save(existingUser);
                }
                return existingUser;
            } else {
                throw new IllegalArgumentException("User already exists with this email using a different login method.");
            }
        }

        appUserRepository.save(newUser);
        return newUser;
    }
}