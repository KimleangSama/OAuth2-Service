package com.kkimleang.authservice.model;

import jakarta.persistence.*;
import java.io.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;

@Getter
@Setter
@ToString
@Entity
@Table(name = "tb_permissions", uniqueConstraints = {@UniqueConstraint(columnNames = {"name"}, name = "unq_name")})
public class Permission extends BaseEntityAudit implements GrantedAuthority, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String name;

    public Permission() {}
    public Permission(String name) {
        this.name = name;
    }

    @Override
    public String getAuthority() {
        return name;
    }
}
