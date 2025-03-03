package com.salapp.ticket.authserver.controller;

import com.salapp.ticket.authserver.config.JwtConfig;
import com.salapp.ticket.authserver.config.JwtUtil;
import com.salapp.ticket.authserver.dto.*;
import com.salapp.ticket.authserver.model.User;
import com.salapp.ticket.authserver.service.TwoFactorService;
import com.salapp.ticket.authserver.service.UserService;
import dev.samstevens.totp.exceptions.QrGenerationException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {
    //private final JwtConfig jwtConfig;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final TwoFactorService twoFactorService;
    private final AuthenticationManager authenticationManager;

    @Value("${service.user.uri}")
    private String URI_USERS_SERVICE;

/*    @Value("${jwt.secret}")
    private String jwtSecret;*/

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        log.info("Login attempt for {}", username);
        // Authenticate user
/*
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
*/

        log.info("Login attempt for {}", username);
        // Fetch user and check 2FA status
        User user = userService.findByUsername(username);
        log.info("user found: {}", user);

        if (!validateAccountActivation(user.getId())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Account not activated");
        }


        if (!user.isTwoFactorEnabled()) {
            try {
                String qrCodeData = twoFactorService.generateQrCodeData(user.getEmail(), user.getTotpSecret());
                return ResponseEntity.ok(new SignupResponse(user.getId(), qrCodeData));
            } catch (QrGenerationException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to generate QR code");
            }
        }
        // If 2FA is already enabled, return a status indicating 2FA is required
        return ResponseEntity.ok(new LoginResponse(user.getId(), "2fa_required"));
    }

    //TODO: change the URI_USERS_SERVICE endpoint to point to the host and port only
    private Boolean validateAccountActivation(String userId) {

        RestTemplate restTemplate = new RestTemplate();
        return Boolean.TRUE.equals(restTemplate.getForEntity("http://localhost:8080" + "/users/" + userId + "/activated", Boolean.class).getBody());

    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        User user = userService.registerLocalUser(request.email(), request.password());
        try {
            String qrCodeData = twoFactorService.generateQrCodeData(user.getEmail(), user.getTotpSecret());

            // Call User-Service to create profile
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<UserProfileRequest> userProfileEntity = new HttpEntity<>(new UserProfileRequest(user.getId(), request.email()));
            ResponseEntity<User> userSaved = restTemplate.postForEntity(URI_USERS_SERVICE, userProfileEntity, User.class);

            log.info("User saved {}", userSaved.getBody());
            return ResponseEntity.status(HttpStatus.CREATED.value()).body(userSaved.getBody());
        } catch (QrGenerationException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to generate QR code");
        }
    }

    @PostMapping("/verify-2fa")
    public ResponseEntity<?> verify2fa(@RequestBody TwoFactorRequest request) {
        User user = userService.findById(request.userId()).orElseThrow(() -> new RuntimeException("User not found"));
        if (twoFactorService.verifyTotpCode(user.getTotpSecret(), request.code())) {
            user.setTwoFactorEnabled(true);
            userService.save(user);

            // Generate JWT
            String token = jwtUtil.generateJwtToken(user);
            return ResponseEntity.ok(new Verify2faResponse(token));
        }
        return ResponseEntity.badRequest().body("Invalid 2FA Code");
    }

    @GetMapping("/api/2fa/qr")
    public ResponseEntity<?> getQrCode(Authentication authentication) throws QrGenerationException {
        User user = userService.findByUsername(authentication.getName());
        String totpUri = twoFactorService.generateQrCodeData(user.getEmail(), user.getTotpSecret());
        return ResponseEntity.ok(new SignupResponse(user.getId(), totpUri));
    }

    @GetMapping("/userinfo")
    public ResponseEntity<?> getUserInfo(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }

        log.info("Authenticated user {}", authentication.getAuthorities());

        String userId = authentication.getName();
        log.info("User info for user {}", userId);
        User user = userService.findByUserID(userId);
        return ResponseEntity.ok(new UserInfoResponse(user.getEmail()));
    }

}

// New DTO for login response


@Getter
class UserInfoResponse {
    private String username;

    public UserInfoResponse(String username) {
        this.username = username;
    }

}

@Getter
class Verify2faResponse {
    private String token;

    public Verify2faResponse(String token) {
        this.token = token;
    }

}