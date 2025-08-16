package com.capstone.ecommerce.user.dto;

public record ResetPasswordConfirmRequest(
        String email,
        String passwordResetToken,
        String newPassword,
        String confirmPassword
) {
}
