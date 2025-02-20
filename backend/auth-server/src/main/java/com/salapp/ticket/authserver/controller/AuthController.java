package com.salapp.ticket.authserver.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AuthController {

    @PostMapping("/api/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        Authentication auth = new UsernamePasswordAuthenticationToken(username, password);
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Redirect to OAuth2 authorize (or return a redirect URL)
        String redirectUrl = "http://localhost:8081/oauth2/authorize?response_type=code&client_id=job-tracker-client&redirect_uri=http://localhost:5173/callback&scope=openid%20read%20write";
        return ResponseEntity.status(HttpStatus.FOUND).header("Location", redirectUrl).build();
    }
}
