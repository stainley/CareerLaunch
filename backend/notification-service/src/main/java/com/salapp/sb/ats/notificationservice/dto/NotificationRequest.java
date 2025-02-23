package com.salapp.sb.ats.notificationservice.dto;

import lombok.Data;

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
