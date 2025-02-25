package com.salapp.job.careerlaunch.userservice.exception;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Standardized error response payload for REST API exceptions.
 * <p>
 * Implements RFC 7807 Problem Details with additional enterprise features.
 * Uses Builder Pattern for safe object construction and field validation.
 *
 * @see <a href="https://tools.ietf.org/html/rfc7807">RFC 7807</a>
 */
@Getter
public class ExceptionResponse {

    /**
     * HTTP status code (required)
     */
    private final int status;

    /**
     * Human-readable error title (required)
     */
    private final String title;

    /**
     * Detailed error description (optional)
     */
    private final String detail;

    /**
     * Application-specific error code (optional)
     */
    private final String errorCode;

    /**
     * Timestamp of error occurrence (auto-generated)
     */
    private final LocalDateTime timestamp;

    /**
     * API endpoint path (optional)
     */
    private final String instance;

    /**
     * Error-specific metadata (optional)
     */
    private final Map<String, Object> parameters;

    /**
     * Validation errors (optional)
     */
    private final List<ValidationError> validationErrors;

    private ExceptionResponse(Builder builder) {
        this.status = builder.status;
        this.title = builder.title;
        this.detail = builder.detail;
        this.errorCode = builder.errorCode;
        this.instance = builder.instance;
        this.parameters = Collections.unmodifiableMap(builder.parameters);
        this.validationErrors = Collections.unmodifiableList(builder.validationErrors);
        this.timestamp = LocalDateTime.now();
    }

    public static Builder builder(int status, String title) {
        return new Builder(status, title);
    }

    /**
     * Builder class with field validation and type safety
     */
    public static class Builder {
        private final int status;
        private final String title;
        private String detail;
        private String errorCode;
        private String instance;
        private Map<String, Object> parameters = Collections.emptyMap();
        private List<ValidationError> validationErrors = Collections.emptyList();

        public Builder(int status, String title) {
            if (status < 400 || status >= 600) {
                throw new IllegalArgumentException("Status code must be in 4xx or 5xx range");
            }
            if (title == null || title.trim().isEmpty()) {
                throw new IllegalArgumentException("Title must not be null or empty");
            }
            this.status = status;
            this.title = title.trim();
        }

        public Builder detail(String detail) {
            this.detail = detail;
            return this;
        }

        public Builder errorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public Builder instance(String instance) {
            this.instance = instance;
            return this;
        }

        public Builder parameters(Map<String, Object> parameters) {
            this.parameters = parameters != null ? parameters : Collections.emptyMap();
            return this;
        }

        public Builder validationErrors(List<ValidationError> validationErrors) {
            this.validationErrors = validationErrors != null ? validationErrors : Collections.emptyList();
            return this;
        }

        public ExceptionResponse build() {
            return new ExceptionResponse(this);
        }
    }

    /**
     * Detailed validation error sub-component
     */
    @Getter
    public static class ValidationError {
        private final String field;
        private final String message;
        private final Object rejectedValue;
        private final String errorCode;

        public ValidationError(String field, String message, Object rejectedValue, String errorCode) {
            if (field == null || message == null) {
                throw new IllegalArgumentException("ValidationError requires field and message");
            }
            this.field = field;
            this.message = message;
            this.rejectedValue = rejectedValue;
            this.errorCode = errorCode;
        }
    }
}