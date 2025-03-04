package com.salapp.ticket.authserver.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Table(name = "permissions", indexes = {
        @Index(name = "idx_permission_name", columnList = "name", unique = true)
})
@Entity
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Long id;

    @Column(unique = true, nullable = false)
    @ToString.Include
    private String name; // e.g., "READ_DATA", "WRITE_DATA"

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "permissions")
    @ToString.Exclude
    @JsonManagedReference
    private Set<Role> roles = new HashSet<>();

}
