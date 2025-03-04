package com.salapp.ticket.authserver.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class GlobalException {

    @ExceptionHandler(BadCredentialsException.class)
    ResponseEntity<ExceptionResponse> handleBadCredentials(BadCredentialsException exception) {
        ExceptionResponse exceptionResponse = ExceptionResponse
                .builder()
                .message(exception.getMessage())
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(exceptionResponse, HttpStatus.UNAUTHORIZED);
    }
}
