package com.salapp.job.careerlaunch.userservice.dto;

import com.salapp.job.careerlaunch.userservice.model.Address;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record ProfileResponse(String email, String firstName, String lastName, String phoneNumber,
                              String profilePictureUrl, String professionalSummary, Address address, String gender, LocalDate birthDate) {
}
