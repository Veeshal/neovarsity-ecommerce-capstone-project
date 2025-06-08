package com.capstone.ecommerce.user.strategy;

import com.capstone.ecommerce.user.entity.AppUser;
import com.capstone.ecommerce.user.entity.SocialLogin;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component("google")
public class GoogleLoginStrategy implements SocialLoginStrategy {

    @Value("${ecom.google.client_id}")
    private String GOOGLE_CLIENT_ID;


    @Override
    public AppUser login(String token) {

        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            String kid = signedJWT.getHeader().getKeyID();

            // Fetch Google's JWK set
            JWKSet jwkSet = JWKSet.load(URI.create("https://www.googleapis.com/oauth2/v3/certs").toURL());
            JWK jwk = jwkSet.getKeyByKeyId(kid);
            if (jwk == null) {
                log.warn("GoogleLoginStrategy: JWK not found for kid: {}", kid);
                throw new IllegalArgumentException("Invalid token: key not found");
            }

            RSAPublicKey publicKey = jwk.toRSAKey().toRSAPublicKey();
            JWSVerifier verifier = new RSASSAVerifier(publicKey);

            if (!signedJWT.verify(verifier)) {
                log.warn("GoogleLoginStrategy: Invalid token signature");
                throw new IllegalArgumentException("Invalid token signature");
            }

            var newUser = createAppUser(signedJWT);
            log.debug("Google user details: {}", newUser);
            return newUser;
        } catch (Exception e) {
            log.warn("GoogleLoginStrategy: Invalid token", e);
            throw new IllegalArgumentException("Invalid token", e);
        }
    }

    private AppUser createAppUser(SignedJWT signedJWT) throws ParseException {
        var claims = signedJWT.getJWTClaimsSet();

        // Validate issuer
        if (!"accounts.google.com".equals(claims.getIssuer()) && !"https://accounts.google.com".equals(claims.getIssuer())) {
            log.warn("GoogleLoginStrategy: Invalid issuer {}", claims.getIssuer());
            throw new IllegalArgumentException("Invalid issuer " + claims.getIssuer());
        }

        // Validate audience
        List<String> audience = claims.getAudience();
        if (!audience.contains(GOOGLE_CLIENT_ID)) {
            log.warn("GoogleLoginStrategy: Invalid audience {}", audience);
            throw new IllegalArgumentException("Invalid audience");
        }

        // Validate expiration time
        if (claims.getExpirationTime().before(new java.util.Date())) {
            log.warn("GoogleLoginStrategy: Token expired at {}", claims.getExpirationTime());
            throw new IllegalArgumentException("Token expired");
        }

        String email = claims.getStringClaim("email");
        String name = claims.getStringClaim("name");

        if (email == null || email.isEmpty()) {
            log.warn("GoogleLoginStrategy: Email claim missing in token");
            throw new IllegalArgumentException("Email claim missing in token");
        }

        var newUser = new AppUser();
        newUser.setEmail(email);
        newUser.setName(name);
        newUser.setSocialLogin(SocialLogin.GOOGLE);
        return newUser;
    }

}