package com.salapp.ticket.authserver.repository;

import com.salapp.ticket.authserver.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByGoogleId(String googleId);

    Optional<User> findByEmail(String email);
}
