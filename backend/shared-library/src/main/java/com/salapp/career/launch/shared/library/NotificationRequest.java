package com.salapp.career.launch.shared.library;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a standardized notification request payload for cross-service communication.
 * <p>
 * This Data Transfer Object (DTO) encapsulates all necessary components for generating
 * and delivering notifications through various channels within the enterprise ecosystem.
 * Designed for JSON serialization/deserialization and validated against enterprise
 * notification standards.
 *
 * @author Stainley Lebron
 * @version 1.0.0
 * @since 2025.2.23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRequest {
    /**
     * Target recipient address in channel-specific format.
     * <p>
     * Must conform to destination channel requirements:
     * <ul>
     *   <li>Email: RFC 5322 compliant address</li>
     *   <li>SMS: E.164 formatted phone number</li>
     *   <li>Push: Device token GUID</li>
     * </ul>
     *
     * @apiNote Required field for all notification types
     */
    @JsonProperty(required = true)
    private String recipient;
    /**
     * Notification channel type identifier.
     * <p>
     * Supported values:
     * <ul>
     *   <li>{@code EMAIL_VERIFICATION}</li>
     *   <li>{@code SMS_ALERT}</li>
     *   <li>{@code PUSH_NOTIFICATION}</li>
     * </ul>
     */
    @JsonProperty(required = true)
    private String messageType;
    /**
     * Dynamic content container for notification personalization.
     * <p>
     * Contains variable data elements that will be interpolated into
     * notification templates during processing. Structure validation
     * is performed against the specified {@link #messageType} template
     * requirements.
     */
    @Valid
    private NotificationData data;

    /**
     * Container for template interpolation variables and security parameters.
     * <p>
     * Provides structured storage for both user-facing content elements
     * and system-generated security tokens. All fields support null values
     * but must adhere to type-specific formatting when present.
     */

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NotificationData {
        /**
         * Recipient's given name for message personalization.
         * <p>
         * Maximum length: 100 characters<br>
         * Format: Unicode letters with diacritics allowed
         *
         * @example "María"
         */
        private String firstName;
        /**
         * Ephemeral security token for verification workflows.
         * <p>
         * Cryptographic requirements:
         * <ul>
         *   <li>Base64URL encoded</li>
         *   <li>Minimum 256-bit entropy</li>
         *   <li>Single-use semantics</li>
         * </ul>
         *
         * @security Requires encryption at rest and in transit
         */
        @JsonProperty(required = true)
        private String token;
        /**
         * Token validity expiration timestamp.
         * <p>
         * Expressed in ISO 8601 extended format with UTC timezone:
         * {@code yyyy-MM-dd'T'HH:mm:ss.SSS'Z'}
         *
         * @example "2024-03-15T23:59:59.999Z"
         */
        @JsonProperty(required = true)
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
        private String expiry;
    }
}
