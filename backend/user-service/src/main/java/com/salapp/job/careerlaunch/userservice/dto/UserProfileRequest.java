package com.salapp.job.careerlaunch.userservice.dto;

import com.salapp.job.careerlaunch.userservice.model.Address;

public record UserProfileRequest(String id, String email, String firstName, String lastName, Address address,
                                 String phoneNumber) {

}
