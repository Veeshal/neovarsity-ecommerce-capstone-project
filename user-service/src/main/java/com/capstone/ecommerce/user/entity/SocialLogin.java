package com.capstone.ecommerce.user.entity;

import lombok.Getter;

@Getter
public enum SocialLogin {
    GOOGLE(1);

    private final int value;

    SocialLogin(int value) {
        this.value = value;
    }


}
