package com.salapp.ticket.authserver.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ResourceController {

    @GetMapping("/protected")
    @PreAuthorize("hasAuthority('SCOPE_read')")
    public String protectedResource() {
        return "This is a protected resource";
    }
}
