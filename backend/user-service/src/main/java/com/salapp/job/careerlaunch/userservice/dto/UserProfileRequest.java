package com.salapp.job.careerlaunch.userservice.dto;

import com.salapp.job.careerlaunch.userservice.model.Address;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserProfileRequest(
        String id,
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,
        String firstName,
        String lastName,
        Address address,
        String phoneNumber
) {

}
