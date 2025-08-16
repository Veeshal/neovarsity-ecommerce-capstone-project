package com.capstone.ecommerce.user.exceptions;

import com.capstone.ecommerce.user.dto.ResponseErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserServiceExceptionHandler {

    @ExceptionHandler(UserServiceRuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseErrorDto handleCommonErrors(Exception e) {
        return new ResponseErrorDto(e.getMessage());
    }
}
