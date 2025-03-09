package com.salapp.job.careerlaunch.userservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
    @Column(unique = true, nullable = false)
    private String email;

    // Must be mandatory
    @Size(max = 100, message = "First name cannot exceed 100 characters")
    @Column(length = 100)
    private String firstName;

    // Must be mandatory
    @Size(max = 255, message = "First name cannot exceed 255 characters")
    @Column()
    private String lastName;

    // Must be mandatory
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    @Column(length = 20)
    private String phoneNumber;

    @Past(message = "Birth date must be in the past")
    @Temporal(TemporalType.DATE)
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Gender gender;

    // Must be mandatory
    @Embedded
    private Address address;

    @Version
    private Long version;

    @Column()
    private String profilePictureUrl;

    @Column(length = 500)
    private String professionalSummary;

    @Column(nullable = false)
    private boolean isActive;

    @Column(length = 300)
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
