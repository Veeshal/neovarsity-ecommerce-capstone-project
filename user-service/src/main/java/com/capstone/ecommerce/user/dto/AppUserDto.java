package com.capstone.ecommerce.user.dto;

import com.capstone.ecommerce.user.entity.AppUser;
import com.capstone.ecommerce.user.entity.SocialLogin;

import java.util.List;

/**
 *
 * Only name & phone are updatable for user profile along with address.
 *
 *
 * @param name
 * @param email
 * @param phone
 * @param socialLogin
 * @param address
 */
public record AppUserDto(String name, String email, String phone, SocialLogin socialLogin, List<AddressDto> address) {
    public static AppUserDto from(AppUser user) {
        return new AppUserDto(user.getName(), user.getEmail(), user.getPhoneNumber(), user.getSocialLogin(), null);
    }
    public static AppUserDto withAddress(AppUser user) {
        return new AppUserDto(
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getSocialLogin(),
                user.getAddresses().stream().map(AddressDto::from).toList()
        );
    }
}
