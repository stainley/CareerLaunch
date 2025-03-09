package com.salapp.ticket.authserver.service;

import com.salapp.ticket.authserver.model.Role;
import com.salapp.ticket.authserver.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public Optional<Role> findByName(String name) {
        return roleRepository.findByName(name);
    }

}
