package com.capstone.ecommerce.user.strategy;

import com.capstone.ecommerce.user.entity.AppUser;
import com.capstone.ecommerce.user.service.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component("microsoft")
public class MicrosoftLoginStrategy implements SocialLoginStrategy {


    @Value("${ecom.microsoft.client_id}")
    private String MICROSOFT_CLIENT_ID;

    private final AppUserService userService;

    @Override
    public AppUser login(String token) {
        // Implement Microsoft login logic here
        throw new UnsupportedOperationException("Microsoft login not implemented yet");
    }
}
