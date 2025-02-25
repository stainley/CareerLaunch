package com.salapp.sb.ats.notificationservice.dto;

import java.time.LocalDateTime;

public record ActivationData(String firstName, String token, LocalDateTime expiry) {
}
