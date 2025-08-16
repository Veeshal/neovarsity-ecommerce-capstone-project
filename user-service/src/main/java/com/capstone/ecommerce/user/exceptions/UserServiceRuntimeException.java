package com.capstone.ecommerce.user.exceptions;

public abstract class UserServiceRuntimeException extends RuntimeException {
    public UserServiceRuntimeException(String message) {
        super(message);
    }

    public UserServiceRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserServiceRuntimeException(Throwable cause) {
        super(cause);
    }
}
