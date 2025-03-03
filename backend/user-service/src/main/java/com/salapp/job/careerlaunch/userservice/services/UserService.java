package com.salapp.job.careerlaunch.userservice.services;

import com.salapp.career.launch.shared.library.NotificationRequest;
import com.salapp.job.careerlaunch.userservice.dto.UserProfileRequest;
import com.salapp.job.careerlaunch.userservice.dto.UserResponse;
import com.salapp.job.careerlaunch.userservice.exception.UserNotFoundException;
import com.salapp.job.careerlaunch.userservice.model.User;
import com.salapp.job.careerlaunch.userservice.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaProducerException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final KafkaTemplate<String, NotificationRequest> kafkaTemplate;

    private static final int TOKEN_EXPIRES_IN_HOURS = 24;
    private static final String NOTIFICATION_TOPIC = "notification-events";

    public User save(User user) {
        String rawToken = UUID.randomUUID().toString();
        String hashedToken = bCryptPasswordEncoder.encode(rawToken);
        LocalDateTime expiry = LocalDateTime.now().plusHours(TOKEN_EXPIRES_IN_HOURS);

        user.setActivationToken(hashedToken);
        user.setActivationTokenExpiry(expiry);
        user.setActive(false);
        User savedUser = userRepository.save(user);

        // Send kafka event for activation notification
        NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setRecipient(user.getEmail());
        notificationRequest.setMessageType("activation");
        NotificationRequest.NotificationData data = new NotificationRequest.NotificationData();
        data.setFirstName(user.getFirstName());
        data.setToken(rawToken);
        data.setExpiry(expiry.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        notificationRequest.setData(data);

        log.info("Sending Kafka message: {}", notificationRequest);
        kafkaTemplate.send(NOTIFICATION_TOPIC, savedUser.getEmail(), notificationRequest);
        return savedUser;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }


    public void delete(User user) {
        userRepository.delete(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> update(User user, String id) {
        User updatedUser = userRepository.findById(id).orElse(null);
        if (updatedUser != null) {
            updatedUser.setFirstName(user.getFirstName());
            updatedUser.setLastName(user.getLastName());
            updatedUser.setEmail(user.getEmail());
            userRepository.save(updatedUser);
        }
        return Optional.ofNullable(updatedUser);
    }

    public User updateProfilePicture(String userId, MultipartFile file) {
        if (userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        String filePath = fileStorageService.storeFile(file);
        user.setProfilePictureUrl(filePath);

        return userRepository.save(user);
    }

    public void activateAccount(String token) {
        User user = userRepository.findAll().stream()
                .filter(u -> u.getActivationToken() != null && bCryptPasswordEncoder.matches(token, u.getActivationToken()))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("Invalid or expired activation token"));

        if (user.isActive()) {
            throw new IllegalStateException("Account is already activated");
        }
        if (user.getActivationTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Activation token has expired");
        }

        user.setActive(true);
        user.setActivationToken(null);
        user.setActivationTokenExpiry(null);
        User activatedUser = userRepository.save(user);

        // Send kafka event for welcome notification
        NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setRecipient(user.getEmail());
        notificationRequest.setMessageType("welcome");
        NotificationRequest.NotificationData data = new NotificationRequest.NotificationData();
        data.setFirstName(user.getFirstName() != null ? user.getFirstName() : "No Name");
        notificationRequest.setData(data);

        kafkaTemplate.send(NOTIFICATION_TOPIC, activatedUser.getEmail(), notificationRequest)
                .whenComplete((result, ex) -> {
                    if (result != null) {
                        handleSuccess(result);
                    } else {
                        handleFailure(ex);
                    }
                });
    }

    private void handleSuccess(SendResult<String, NotificationRequest> result) {
        log.info("Successfully sent message to partition {} with offset {}",
                result.getRecordMetadata().partition(),
                result.getRecordMetadata().offset());
    }

    private void handleFailure(Throwable ex) {
        if (ex instanceof KafkaProducerException kpe) {
            log.error("Failed to send message: {}", kpe.getFailedProducerRecord().value());
        } else {
            log.error("Unexpected error: {}", ex.getMessage());
        }
    }

    public UserResponse updateUserProfile(String userId, UserProfileRequest request) {
        User userFound = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        userFound.setFirstName(request.firstName());
        userFound.setLastName(request.lastName());
        userFound.setPhoneNumber(request.phoneNumber());
        userFound.setAddress(request.address());
        userRepository.save(userFound);

        return new UserResponse("User updated successfully", null);
    }
}
