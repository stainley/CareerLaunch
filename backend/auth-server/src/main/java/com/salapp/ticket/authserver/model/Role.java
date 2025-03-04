package com.salapp.ticket.authserver.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;


@Table(name = "roles", indexes = {
        @Index(name = "idx_role_name", columnList = "name", unique = true),
})
@Getter
@Setter
@Entity
@ToString(onlyExplicitlyIncluded = true)
public class Role {

    @ToString.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Include
    @Column(nullable = false, unique = true)
    private String name; //ADMIN, USER

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "roles")
    @ToString.Exclude
    @JsonManagedReference
    private Set<User> users = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "role_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    @ToString.Exclude
    @JsonManagedReference
    private Set<Permission> permissions = new HashSet<>();

}
