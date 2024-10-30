package com.kkimleang.authservice.model;

import com.kkimleang.authservice.enumeration.AuthProvider;
import com.redis.om.spring.annotations.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RedisHash("User")
@Getter
@Setter
@ToString
@Entity
@Table(name = "tb_users", uniqueConstraints = {@UniqueConstraint(columnNames = {"email"}, name = "unq_email")})
public class User extends BaseEntityAudit {
    @Serial
    private static final long serialVersionUID = 1L;

    private String username;
    private String password;

    @NotNull
    @Indexed
    private String email;

    @Column(name = "profile_url")
    private String profileURL;

    @Column(name = "provider_id")
    private String providerId;

    @Column(name = "is_enabled")
    private Boolean isEnabled;

    @Column(name = "is_verified")
    private Boolean isVerified;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE})
    @JoinTable(name = "tb_users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();
}
