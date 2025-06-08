package com.capstone.ecommerce.user.dto;

public record SocialLoginResponse(String token, boolean emailMissing, boolean nameMissing) {
}
