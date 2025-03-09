package com.salapp.ticket.authserver.config;

import com.salapp.ticket.authserver.model.Permission;
import com.salapp.ticket.authserver.model.Role;
import com.salapp.ticket.authserver.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private long jwtExpiration;
    private long sessionExpiration;

    public String generateJwtToken(User user) {
        List<String> roles = new ArrayList<>();
        List<String> authorities = new ArrayList<>();
        log.info("Generate JWT Token for user: {}", user.getId());

        // Populate roles and authorities
        for (Role role : user.getRoles()) {
            String roleName = "ROLE_" + role.getName().toUpperCase(); // e.g., ROLE_USER, ROLE_ADMIN
            roles.add(roleName);
            for (Permission permission : role.getPermissions()) {
                authorities.add(permission.getName().toUpperCase()); // e.g., READ, WRITE
            }
        }

        // Remove duplicates
        Set<String> uniqueRoles = Set.copyOf(roles);
        Set<String> uniqueAuthorities = Set.copyOf(authorities);

        return Jwts.builder()
                .setSubject(user.getId())                          // User ID as subject
                .claim("email", user.getEmail())                  // Email claim
                .claim("roles", uniqueRoles)                      // Roles claim
                .claim("authorities", uniqueAuthorities)          // Authorities (permissions) claim
                .setIssuer("http://localhost:8080/auth")          // Issuer
                .setIssuedAt(new Date())                          // Issued at
                .setExpiration(new Date(System.currentTimeMillis() + 3_600_000)) // 1-hour expiration
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()), SignatureAlgorithm.HS256) // Signing
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            log.info("Validating JWT token");
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(jwtSecret.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            if (claims.getExpiration().before(new Date())) {
                log.warn("Token has expired");
                return false;
            }

            String username = claims.getSubject();
            @SuppressWarnings("unchecked")
            List<String> authorities = claims.get("authorities", List.class);

            List<GrantedAuthority> grantedAuthorities = authorities != null
                    ? authorities.stream()
                    .filter(Objects::nonNull)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList())
                    : Collections.emptyList();

            // Avoid setting SecurityContextHolder if not needed here
            //Authentication auth = new UsernamePasswordAuthenticationToken(username, null, grantedAuthorities);
            //SecurityContextHolder.getContext().setAuthentication(auth);
            return true;
        } catch (JwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }
}
