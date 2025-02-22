package com.salapp.job.careerlaunch.userservice.controller;

import com.salapp.job.careerlaunch.userservice.dto.UserProfileRequest;
import com.salapp.job.careerlaunch.userservice.model.User;
import org.springframework.http.ResponseEntity;

public interface IUserController {

    ResponseEntity<User> getUserById(String id);

    ResponseEntity<User> getUserByEmail(String email);

    ResponseEntity<User> createUser(UserProfileRequest request);
}
