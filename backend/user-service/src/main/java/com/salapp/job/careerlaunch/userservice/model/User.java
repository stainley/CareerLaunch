package com.salapp.job.careerlaunch.userservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_users_id", columnList = "id", unique = true),
        @Index(name = "idx_users_email", columnList = "email", unique = true)
})
public class User {

    @Id
    private String id;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Column(unique = true, nullable = false, length = 255)
    private String email;

    @Size(max = 100, message = "First name cannot exceed 100 characters")
    @Column(length = 100)
    private String firstName;

    @Size(max = 255, message = "First name cannot exceed 255 characters")
    @Column(length = 255)
    private String lastName;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    @Column(length = 20)
    private String phoneNumber;

    @Past(message = "Birth date must be in the past")
    @Temporal(TemporalType.DATE)
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Gender gender;

    @Embedded
    private Address address;

    @Version
    private Long version;

    @Column(length = 255)
    private String profilePictureUrl;

    @Column(nullable = false)
    private boolean isActive;

    @Column(length = 255)
    private String activationToken;

    @Column
    private LocalDateTime activationTokenExpiry;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(updatable = false)
    private LocalDateTime updatedAt;

    public static class UserBuilder {
        private LocalDateTime createdAt = LocalDateTime.now();
        private LocalDateTime updatedAt = LocalDateTime.now();
    }

}
