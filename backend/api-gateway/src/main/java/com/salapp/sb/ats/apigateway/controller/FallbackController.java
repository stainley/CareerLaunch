package com.salapp.sb.ats.apigateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/user")
    public String userFallback() {
        return "User Service is currently unavailable. Please try again later.";
    }

    @GetMapping("/auth")
    public String authFallback() {
        return "Auth Service is currently unavailable. Please try again later";
    }
}
