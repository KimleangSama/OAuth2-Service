package com.kkimleang.authservice.repository;

import com.kkimleang.authservice.model.User;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByIdIn(List<UUID> ids);

    @Modifying
    @Query("UPDATE User u SET u.profileURL = :profileURL WHERE u.id = :id")
    Integer updateUserProfile(UUID id, String profileURL);
}
