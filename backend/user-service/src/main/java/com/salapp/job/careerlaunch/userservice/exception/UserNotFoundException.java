package com.salapp.job.careerlaunch.userservice.exception;

/**
 * Custom exception class to indicate that a user could not be found.
 * This exception is typically used in scenarios where a requested user
 * does not exist in the system (e.g., during authentication or data retrieval).
 */
public class UserNotFoundException extends RuntimeException {

    /**
     * Constructs a new {@code UserNotFoundException} with the specified detail message.
     *
     * @param message the detail message explaining why this exception was thrown.
     *                This message can be retrieved later using the {@link #getMessage()} method.
     */
    public UserNotFoundException(String message) {
        super(message);
    }
}
