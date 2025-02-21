package com.salapp.ticket.authserver.dto;

public record TwoFactorRequest(String userId, String code) {
}
