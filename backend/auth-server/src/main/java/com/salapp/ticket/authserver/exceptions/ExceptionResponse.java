package com.salapp.ticket.authserver.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
public class ExceptionResponse {
    private int statusCode;
    private String message;
    private LocalDateTime timestamp;
    private Exception exception;

    public ExceptionResponse(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}
