package com.capstone.ecommerce.product.exceptions;

import com.capstone.ecommerce.product.dto.BadRequestErrorDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ProductGlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<BadRequestErrorDto> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.badRequest()
                .body(new BadRequestErrorDto(ex.getMessage()));
    }
}
