package com.salapp.job.careerlaunch.userservice.controller;

import com.salapp.job.careerlaunch.userservice.dto.ProfileResponse;
import com.salapp.job.careerlaunch.userservice.dto.UserProfileRequest;
import com.salapp.job.careerlaunch.userservice.dto.UserResponse;
import com.salapp.job.careerlaunch.userservice.model.Address;
import com.salapp.job.careerlaunch.userservice.model.User;
import com.salapp.job.careerlaunch.userservice.services.UserService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController implements IUserController {

    private final UserService userService;
    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_ROLES = "X-Roles";
    private static final String HEADER_PERMISSIONS = "X-Permissions";

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
    public ResponseEntity<User> createUser(@Valid @RequestBody UserProfileRequest request) {

        log.info("Creating user: {}", request);

        User user = User.builder()
                .id(request.id())
                .email(request.email())
                .build();

        User savedUser = userService.save(user);

        return ResponseEntity.status(HttpStatus.CREATED.value()).body(savedUser);
    }

    @PostMapping("/profile-picture/")
    @Override
    public ResponseEntity<User> uploadProfilePicture(@RequestParam("file") MultipartFile file,
                                                     @RequestHeader(HEADER_USER_ID) String userId) {
        log.info("Uploading profile picture: {} \nFor userId: {}", file, userId);
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

    @PutMapping("/profile")
    @Override
    public ResponseEntity<UserResponse> updateUserProfile(
            @Valid @RequestBody UserProfileRequest request,
            @RequestHeader(HEADER_USER_ID) String userId,
            @RequestHeader(HEADER_ROLES) String rolesHeader,
            @RequestHeader(HEADER_PERMISSIONS) String permissionsHeader) {

        if (userId == null || rolesHeader == null || permissionsHeader == null) {
            log.warn("Missing required headers from API Gateway for user ID: {}", userId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new UserResponse("Missing authentication headers", null));
        }

        List<String> roles = Arrays.asList(rolesHeader.split(","));
        List<String> permissions = Arrays.asList(permissionsHeader.split(","));

        if (!permissions.contains("PROFILE_UPDATE")) {
            log.warn("User ID {} lacks PROFILE_UPDATE permission", userId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new UserResponse("Permission denied", null));
        }

        try {
            UserResponse updatedUser = userService.updateUserProfile(userId, request);
            log.info("Successfully updated profile for user ID: {}", userId);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            log.error("Unexpected error updating profile for user ID: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new UserResponse("Internal server error", null));
        }
    }

    @GetMapping(value = "/profile/info")
    @Override
    public ResponseEntity<ProfileResponse> profileInfo(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader(value = "X-Roles", required = false) String roles,
            @RequestHeader(value = "X-Permissions", required = false) String permissions
    ) {

        log.info("Retrieving profile info for user ID: {}", userId);
        log.info("Retrieving profile info for roles: {}", roles);
        log.info("Retrieving profile info for permissions: {}", permissions);

        User userFound = userService.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        ProfileResponse profileResponse = ProfileResponse.builder()
                .email(userFound.getEmail())
                .firstName(userFound.getFirstName())
                .lastName(userFound.getLastName())
                .phoneNumber(userFound.getPhoneNumber())
                .gender(userFound.getGender() != null ? userFound.getGender().toString() : null)
                .birthDate(userFound.getBirthDate())
                .profilePictureUrl(userFound.getProfilePictureUrl() != null ? userFound.getProfilePictureUrl() : null)
                .professionalSummary(userFound.getProfessionalSummary())
                .address(mapAddress(userFound))
                .build();

        log.info("User profile info for user ID: {} - {}", userId, profileResponse);

        return ResponseEntity.ok(profileResponse);
    }

    @DeleteMapping
    @Override
    public ResponseEntity<String> deleteUser(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader(value = "X-Roles", required = false) String roles,
            @RequestHeader(value = "X-Permissions", required = false) String permissions
    ) {
        log.info("Deleting user: {}", userId);
        User user = userService.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        userService.delete(user);
        return ResponseEntity.ok("User deleted successfully");
    }

    private Address mapAddress(User user) {
        if (user.getAddress() != null) {
            return Address.builder()
                    .city(user.getAddress().getCity() != null ? user.getAddress().getCity() : "")
                    .country(user.getAddress().getCountry() != null ? user.getAddress().getCountry() : "")
                    .country(user.getAddress().getCountry() != null ? user.getAddress().getCountry() : "")
                    .postalCode(user.getAddress().getPostalCode() != null ? user.getAddress().getPostalCode() : "")
                    .street(user.getAddress().getStreet() != null ? user.getAddress().getStreet() : "")
                    .stateOrProvince(user.getAddress().getStateOrProvince() != null ? user.getAddress().getStateOrProvince() : "")
                    .build();
        }

        return Address.builder()
                .build();
    }

    //TODO: Move the validation to the services
    @GetMapping("/{token}/activated")
    public ResponseEntity<Boolean> validationAccountActivation(@PathVariable String token) {
        User user = userService.findById(token).orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.status(HttpStatus.OK).body(user.isActive());
    }

    @Getter
    private static class AuthResponse {
        private final boolean isValid;
        private final String userId;
        private final List<String> roles;
        private final List<String> permissions;

        public AuthResponse(boolean isValid, String userId, List<String> roles, List<String> permissions) {
            this.isValid = isValid;
            this.userId = userId;
            this.roles = roles != null ? roles : Collections.emptyList();
            this.permissions = permissions != null ? permissions : Collections.emptyList();
        }

    }
}
