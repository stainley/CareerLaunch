package com.salapp.career.launch.library;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.salapp.career.launch.shared.library.NotificationRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class NotificationRequestTest {
    private ObjectMapper objectMapper;
    private Validator validator;

    @BeforeEach
    void setUp() {
        // Initialize Jackson ObjectMapper
        objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        // Initialize Jakarta Validation
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testValidNotificationRequest() {
        // Arrange
        NotificationRequest.NotificationData data = new NotificationRequest.NotificationData(
                "John",
                "validBase64Token123",
                "2025-03-15T23:59:59.999999"
        );

        NotificationRequest request = new NotificationRequest(
                "john.doe@example.com",
                "EMAIL_VERIFICATION",
                data
        );

        // Act
        Set<ConstraintViolation<NotificationRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty(), "Valid notification request should have no validation errors");
        assertEquals("john.doe@example.com", request.getRecipient());
        assertEquals("EMAIL_VERIFICATION", request.getMessageType());
        assertEquals("John", request.getData().getFirstName());
        assertEquals("validBase64Token123", request.getData().getToken());
        assertEquals("2025-03-15T23:59:59.999999", request.getData().getExpiry());
    }

    @Test
    void testMissingRequiredFields() {
        // Arrange: Missing required fields
        NotificationRequest request = new NotificationRequest();

        // Act
        Set<ConstraintViolation<NotificationRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty(), "Should have validation errors for missing required fields");
        assertEquals(2, violations.size(), "Should have violations for recipient and messageType");
        //assertEquals(2, violations.size(), "Should have violations for recipient and messageType");
    }

    @Test
    void testJsonSerialization() throws Exception {
        // Arrange
        NotificationRequest.NotificationData data = new NotificationRequest.NotificationData(
                "María",
                "base64TokenExample",
                "2025-03-15T23:59:59.999999"
        );

        NotificationRequest request = new NotificationRequest(
                "+12025550123",
                "SMS_ALERT",
                data
        );

        // Act
        String json = objectMapper.writeValueAsString(request);

        // Assert
        String expectedJson = "{\n" +
                "  \"recipient\" : \"+12025550123\",\n" +
                "  \"messageType\" : \"SMS_ALERT\",\n" +
                "  \"data\" : {\n" +
                "    \"firstName\" : \"María\",\n" +
                "    \"token\" : \"base64TokenExample\",\n" +
                "    \"expiry\" : \"2025-03-15T23:59:59.999999\"\n" +
                "  }\n" +
                "}";
        assertEquals(expectedJson.replaceAll("\\s", ""), json.replaceAll("\\s", ""),
                "JSON serialization should match expected format");
    }

    @Test
    void testJsonDeserialization() throws Exception {
        // Arrange
        String json = "{" +
                "\"recipient\": \"user@domain.com\"," +
                "\"messageType\": \"EMAIL_VERIFICATION\"," +
                "\"data\": {" +
                "\"firstName\": \"Jane\"," +
                "\"token\": \"secureToken123\"," +
                "\"expiry\": \"2025-03-15T23:59:59.999999\"" +
                "}" +
                "}";

        // Act
        NotificationRequest request = objectMapper.readValue(json, NotificationRequest.class);

        // Assert
        assertNotNull(request);
        assertEquals("user@domain.com", request.getRecipient());
        assertEquals("EMAIL_VERIFICATION", request.getMessageType());
        assertEquals("Jane", request.getData().getFirstName());
        assertEquals("secureToken123", request.getData().getToken());
        assertEquals("2025-03-15T23:59:59.999999", request.getData().getExpiry());
    }

    @Test
    void testNotificationDataWithNullValues() {
        // Arrange
        NotificationRequest.NotificationData data = new NotificationRequest.NotificationData(
                null,  // firstName can be null
                "requiredToken",
                "2025-03-15T23:59:59.999999"
        );

        NotificationRequest request = new NotificationRequest(
                "push:device123",
                "PUSH_NOTIFICATION",
                data
        );

        // Act
        Set<ConstraintViolation<NotificationRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty(), "NotificationData with null firstName should be valid");
        assertNull(request.getData().getFirstName());
        assertNotNull(request.getData().getToken());
        assertNotNull(request.getData().getExpiry());
    }

}
