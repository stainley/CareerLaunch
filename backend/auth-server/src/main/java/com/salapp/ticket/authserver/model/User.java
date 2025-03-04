package com.salapp.ticket.authserver.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;


@Entity
@Getter
@Setter
@Table(name = "users", indexes = {
        @Index(name = "idx_users_email", columnList = "email", unique = true),
        @Index(name = "idx_users_google_id", columnList = "google_id", unique = true),
})
@ToString(onlyExplicitlyIncluded = true)
public class User {

    @Id
    @ToString.Include
    private String id; // UUID or email

    @ToString.Include
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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @ToString.Exclude
    @JsonManagedReference
    private Set<Role> roles = new HashSet<>();

}
