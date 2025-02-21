package com.salapp.ticket.authserver.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "users", indexes = {
        @Index(name = "idx_users_email", columnList = "email", unique = true),
        @Index(name = "idx_users_google_id", columnList = "google_id", unique = true),
})
public class User {

    @Id
    private String id; // UUID or email
    private String email;
    private String passwordHash; // Hashed with bcrypt
    private String googleId;    // Nullable, for Google users
    private String totpSecret;  // For 2FA
    private boolean twoFactorEnabled;

}
