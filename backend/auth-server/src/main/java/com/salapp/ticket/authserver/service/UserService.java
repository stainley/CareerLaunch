package com.salapp.ticket.authserver.service;

import com.salapp.ticket.authserver.model.Role;
import com.salapp.ticket.authserver.model.User;
import com.salapp.ticket.authserver.repository.RoleRepository;
import com.salapp.ticket.authserver.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TwoFactorService twoFactorService;
    private final RoleRepository roleRepository;

    private static final String DEFAULT_ROLE = "USER";

    public User registerLocalUser(String email, String password) {
        User user = new User();
        user.setId(java.util.UUID.randomUUID().toString());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setTotpSecret(twoFactorService.generateSecret());
        user.setTwoFactorEnabled(false);

        // Assign default role
        Set<Role> roles = new HashSet<>();
        Role defaultRole = roleRepository.findByName(DEFAULT_ROLE)
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName(DEFAULT_ROLE);
                    return roleRepository.save(newRole);
                });
        roles.add(defaultRole);
        user.setRoles(roles);


        return userRepository.save(user);
    }

    public User findOrCreateGoogleUser(String googleId, String email) {
        return userRepository.findByGoogleId(googleId)
                .orElseGet(() -> {
                    User user = new User();
                    user.setGoogleId(UUID.randomUUID().toString());
                    user.setEmail(email);
                    user.setGoogleId(googleId);
                    user.setTotpSecret(twoFactorService.generateSecret());
                    user.setTwoFactorEnabled(false);

                    // Assign default role for Google users
                    Set<Role> roles = new HashSet<>();
                    Role defaultRole = roleRepository.findByName(DEFAULT_ROLE)
                            .orElseGet(() -> {
                                Role newRole = new Role();
                                newRole.setName(DEFAULT_ROLE);
                                return roleRepository.save(newRole);
                            });
                    roles.add(defaultRole);
                    user.setRoles(roles);

                    return userRepository.save(user);
                });
    }

    public Optional<User> findById(String userId) {
        return userRepository.findById(userId);
    }

    public User findByUsername(String username) {
        log.info("Finding user by name: {}", username);
        return userRepository.findByEmail(username).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User findByUserID(String userId) {
        log.info("Finding user by ID: {}", userId);
        return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void save(User user) {
        userRepository.save(user);
    }

    @PostConstruct
    public void initDefaultRoles() {
        if (roleRepository.findByName(DEFAULT_ROLE).isEmpty()) {
            Role defaultRole = new Role();
            defaultRole.setName(DEFAULT_ROLE);
            roleRepository.save(defaultRole);
            log.info("Creating default role: {}", DEFAULT_ROLE);
        }
    }

}
