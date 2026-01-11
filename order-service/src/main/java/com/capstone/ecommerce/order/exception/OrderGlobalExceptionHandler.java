package com.capstone.ecommerce.order.exception;

import com.capstone.ecommerce.order.dto.ResponseErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class OrderGlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseErrorDto handleEmptyCartException(RuntimeException ex) {
        return new ResponseErrorDto(ex.getMessage());
    }
}
