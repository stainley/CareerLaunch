package com.salapp.job.careerlaunch.userservice.dto;

import lombok.Builder;

@Builder
public record ProfileRequest(String id, String email, String firstName, String lastName, String professionalSummary) {

}
