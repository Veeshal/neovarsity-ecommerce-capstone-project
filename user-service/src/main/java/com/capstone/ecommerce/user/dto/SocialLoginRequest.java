package com.capstone.ecommerce.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SocialLoginRequest(
        @NotBlank
        @Pattern(regexp = "^(google|meta|microsoft)$", flags = Pattern.Flag.CASE_INSENSITIVE,
                 message = "Provider must be one of: google, meta, microsoft")
        String provider,

        @NotBlank
        String token
) {}
