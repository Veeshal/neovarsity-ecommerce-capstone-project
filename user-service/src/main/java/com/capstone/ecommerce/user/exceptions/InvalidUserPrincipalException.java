package com.capstone.ecommerce.user.exceptions;

public class InvalidUserPrincipalException extends UserServiceRuntimeException {
    public InvalidUserPrincipalException(String email) {
        super("Invalid user principal: " + email);
    }
}
