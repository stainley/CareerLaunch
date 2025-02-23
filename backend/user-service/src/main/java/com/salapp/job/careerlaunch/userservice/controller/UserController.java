package com.salapp.job.careerlaunch.userservice.controller;

import com.salapp.job.careerlaunch.userservice.dto.UserProfileRequest;
import com.salapp.job.careerlaunch.userservice.model.User;
import com.salapp.job.careerlaunch.userservice.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController implements IUserController {
    private final UserService userService;

    @Override
    @GetMapping
    public ResponseEntity<User> getUserById(String id) {
        User userFound = userService.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(userFound);
    }

    @Override
    public ResponseEntity<User> getUserByEmail(String email) {
        User userFound = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(userFound);
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<User> createUser(@RequestBody UserProfileRequest request) {

        log.info("Creating user: {}", request);

        User user = User.builder()
                .id(request.id())
                .email(request.email())
                .build();

        User createdUser = userService.save(user);

        return ResponseEntity.ok(createdUser);
    }

    @PostMapping("/{userId}/profile-picture/")
    @Override
    public ResponseEntity<User> uploadProfilePicture(@PathVariable String userId, @RequestParam("file") MultipartFile file) {
        User updatedUser = userService.updateProfilePicture(userId, file);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/activate")
    @Override
    public ResponseEntity<String> activateAccount(@RequestParam("token") String token) {
        log.info("Activating account with token: {}", token);
        userService.activateAccount(token);
        return ResponseEntity.ok("Account activated successfully");
    }


}
