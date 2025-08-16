package com.capstone.ecommerce.user.exceptions;

public class UserDoesNotExistException extends UserServiceRuntimeException {
    public UserDoesNotExistException(String message) {
        super(message);
    }
}
