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

import java.net.URI;
import java.security.interfaces.RSAPublicKey;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AppUserService implements UserDetailsService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${ecom.google_client_id}")
    private String GOOGLE_CLIENT_ID;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return appUserRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
    }

    public AppUser registerUser(String email, String password, String name) {
        var user = appUserRepository.findByEmail(email);

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


    public AppUser registerGoogleUser(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            String kid = signedJWT.getHeader().getKeyID();


            // Fetch Google's JWK set
            JWKSet jwkSet = JWKSet.load(URI.create("https://www.googleapis.com/oauth2/v3/certs").toURL());
            JWK jwk = jwkSet.getKeyByKeyId(kid);
            if (jwk == null) throw new IllegalArgumentException("Invalid token: key not found");

            RSAPublicKey publicKey = jwk.toRSAKey().toRSAPublicKey();
            JWSVerifier verifier = new RSASSAVerifier(publicKey);

            if (!signedJWT.verify(verifier)) throw new IllegalArgumentException("Invalid token signature");

            var claims = signedJWT.getJWTClaimsSet();

            // Validate issuer and audience
            if (!"accounts.google.com".equals(claims.getIssuer())) {
                throw new IllegalArgumentException("Invalid issuer " + claims.getIssuer());
            }
            List<String> audience = claims.getAudience();
            if (!audience.contains(GOOGLE_CLIENT_ID)) {
                throw new IllegalArgumentException("Invalid audience");
            }
            if (claims.getExpirationTime().before(new java.util.Date())) {
                throw new IllegalArgumentException("Token expired");
            }

            String email = claims.getStringClaim("email");
            String name = claims.getStringClaim("name");

            var user = appUserRepository.findByEmail(email);
            if (user.isPresent()) return user.get();

            var newUser = new AppUser();
            newUser.setEmail(email);
            newUser.setName(name);
            newUser.setPassword(""); // Or random
            newUser.setSocialLogin(SocialLogin.GOOGLE);
            appUserRepository.save(newUser);
            return newUser;
        } catch (Exception e) {
            throw new RuntimeException("Failed to register Google user", e);
        }
    }
}
