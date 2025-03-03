package com.salapp.ticket.authserver.config;

import com.salapp.ticket.authserver.model.Permission;
import com.salapp.ticket.authserver.model.Role;
import com.salapp.ticket.authserver.model.User;
import io.jsonwebtoken.Claims;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    public void validateToken(String token) {
        log.info("Validate Token");
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecret.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();

        String username = claims.getSubject();
        List<String> authorities = (List<String>) claims.get("authorities");

        List<GrantedAuthority> grantedAuthorities = authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        Authentication auth = new UsernamePasswordAuthenticationToken(username, null, grantedAuthorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

}
