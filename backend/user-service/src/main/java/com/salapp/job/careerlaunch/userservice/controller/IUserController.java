package com.salapp.job.careerlaunch.userservice.controller;

import com.salapp.job.careerlaunch.userservice.dto.UserProfileRequest;
import com.salapp.job.careerlaunch.userservice.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * Interface defining the contract for user-related API endpoints.
 */
public interface IUserController {

    ResponseEntity<User> getUserById(String id);

    ResponseEntity<User> getUserByEmail(String email);

    ResponseEntity<User> createUser(UserProfileRequest request);

    ResponseEntity<User> uploadProfilePicture(String userId, MultipartFile file);

    ResponseEntity<String> activateAccount(String token);
}
