package com.salapp.job.careerlaunch.userservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Global exception handler for the application, providing consistent error responses.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles UserNotFoundException when a user is not found.
     *
     * @param exception The exception thrown
     * @return ResponseEntity containing the structured error response
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleNotFoundException(final UserNotFoundException exception) {
        log.error("User not found: {}", exception.getMessage());

        ExceptionResponse exceptionResponse = ExceptionResponse.builder(HttpStatus.NOT_FOUND.value(), "User not found")
                .detail(exception.getMessage())
                .errorCode("USER_NOT_FOUND")
                .build();

        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles IllegalArgumentException for invalid input parameters.
     *
     * @param ex The exception thrown
     * @return ResponseEntity containing the structured error response
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Invalid argument: {}", ex.getMessage());
        ExceptionResponse response = ExceptionResponse.builder(HttpStatus.BAD_REQUEST.value(), "Ensure all parameters are valid")
                .detail(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handles validation errors from @Valid annotations.
     *
     * @param ex The validation exception
     * @return ResponseEntity containing the structured error response with validation details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.warn("Validation failed: {}", ex.getMessage());
        String details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ExceptionResponse response = ExceptionResponse.builder(
                        HttpStatus.BAD_REQUEST.value(),
                        "Validation failed")
                .detail(details)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Fallback handler for any uncaught exceptions.
     *
     * @param ex The exception thrown
     * @return ResponseEntity containing a generic error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        ExceptionResponse response = ExceptionResponse.builder(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "An unexpected error occurred")
                .detail(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

/***
 * // Basic error
 * ExceptionResponse error = ExceptionResponse.builder(404, "Resource Not Found")
 *     .detail("User with ID 12345 not found")
 *     .instance("/api/users/12345")
 *     .errorCode("USER-001")
 *     .build();
 * <p>
 * // Validation error
 * List<ExceptionResponse.ValidationError> validationErrors = List.of(
 *     new ExceptionResponse.ValidationError(
 *         "email",
 *         "Must be a valid email address",
 *         "invalid.email",
 *         "VALIDATION-101"
 *     )
 * );
 * <p>
 * ExceptionResponse validationError = ExceptionResponse.builder(400, "Invalid Request")
 *     .detail("Request contains validation errors")
 *     .validationErrors(validationErrors)
 *     .parameters(Map.of("retryable", false))
 *     .build();
 *
 */
