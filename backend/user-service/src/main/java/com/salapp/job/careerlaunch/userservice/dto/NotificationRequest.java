package com.salapp.job.careerlaunch.userservice.dto;

import lombok.Data;

// TODO: Remove this class and notification and create a shared library
@Data
public class NotificationRequest {
    private String recipient;
    private String messageType;
    private NotificationData data;

    @Data
    public static class NotificationData {
        private String firstName;
        private String token;
        private String expiry;
    }
}
