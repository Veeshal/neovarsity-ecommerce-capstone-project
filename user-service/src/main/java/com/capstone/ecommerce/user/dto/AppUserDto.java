package com.capstone.ecommerce.user.dto;

import com.capstone.ecommerce.user.entity.AppUser;
import com.capstone.ecommerce.user.entity.SocialLogin;

public record AppUserDto(String name, String email, String phone, SocialLogin socialLogin) {
    public static AppUserDto from(AppUser user) {
        return new AppUserDto(user.getName(), user.getEmail(), user.getPhoneNumber(), user.getSocialLogin());
    }
}
