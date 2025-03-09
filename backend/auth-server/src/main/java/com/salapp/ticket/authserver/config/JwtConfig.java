package com.salapp.ticket.authserver.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;

@Configuration
public class JwtConfig {

    // Generate proper 256-bit (32-byte) key using secure random bytes
    private static final String BASE64_SECRET = generateSecureSecret();

    // Convert to proper SecretKey type for HMAC-SHA algorithms
    private static final SecretKey JWT_SECRET_KEY = Keys.hmacShaKeyFor(
            Base64.getDecoder().decode(BASE64_SECRET)
    );

    // Generate cryptographically secure random key
    private static String generateSecureSecret() {
        byte[] keyBytes = new byte[32]; // 256 bits
        new SecureRandom().nextBytes(keyBytes);
        return Base64.getEncoder().encodeToString(keyBytes);
    }

    @Bean
    public SecretKey getJwtSecretKey() {
        return JWT_SECRET_KEY;
    }


}
