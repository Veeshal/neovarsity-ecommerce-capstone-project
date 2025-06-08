package com.capstone.ecommerce.user.strategy;

import com.capstone.ecommerce.user.entity.AppUser;
import com.capstone.ecommerce.user.service.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component("facebook")
public class FacebookLoginStrategy implements SocialLoginStrategy {


    @Value("${ecom.meta.client_id}")
    private String META_CLIENT_ID;

    private final AppUserService userService;

    @Override
    public AppUser login(String token) {
        // Implement Facebook login logic here
        throw new UnsupportedOperationException("Facebook login not implemented yet");
    }
}
