package com.capstone.ecommerce.user.exceptions;

public class UsernamePasswordAuthenticationException extends UserServiceRuntimeException {
    public UsernamePasswordAuthenticationException(String message) {
        super(message);
    }
}
