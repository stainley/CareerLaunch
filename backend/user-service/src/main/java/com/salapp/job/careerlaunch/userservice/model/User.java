package com.salapp.job.careerlaunch.userservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_users_email", columnList = "email")
})
public class User {

    @Id
    private String id;
    @Email
    @Column(unique = true, nullable = false)
    private String email;
    private String firstName;
    private String lastName;

    private String phoneNumber;
    @Temporal(TemporalType.TIMESTAMP)
    private Date birthDate;

    private String gender;
    private String address;

    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private LocalDateTime createdAt;
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private LocalDateTime updatedAt;


}
