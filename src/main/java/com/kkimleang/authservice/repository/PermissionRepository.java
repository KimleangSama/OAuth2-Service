package com.kkimleang.authservice.repository;

import com.kkimleang.authservice.model.Permission;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, UUID> {
    Permission findByName(String name);
}
