package com.capstone.ecommerce.user.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SocialLogin {
    NONE(0, "none", "No Social Login"),
    GOOGLE(1, "google", "Google LLC"),
    FACEBOOK(2, "facebook", "Meta Platforms"),
    MICROSOFT(3, "microsoft", "Microsoft Corporation");;

    private final int id;
    @JsonValue
    private final String code;
    private final String description;



    @JsonCreator
    public static SocialLogin fromCode(String code) {
        for (SocialLogin socialLogin : values()) {
            if (socialLogin.getCode().equalsIgnoreCase(code)) {
                return socialLogin;
            }
        }
        throw new IllegalArgumentException("Unknown social login code: " + code);
    }
}
