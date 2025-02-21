package com.salapp.ticket.authserver.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;

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

    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private Date updatedAt;

}
