package com.kkimleang.authservice.repository;

import com.kkimleang.authservice.model.Role;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByName(String name);
    List<Role> findByNameIn(List<String> names);
}
