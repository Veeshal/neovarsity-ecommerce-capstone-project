package com.capstone.ecommerce.user.strategy;

import com.capstone.ecommerce.user.entity.AppUser;

public interface SocialLoginStrategy {
    AppUser login(String token);
}
