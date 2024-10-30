package com.kkimleang.authservice.service.user;

import com.kkimleang.authservice.exception.ResourceNotFoundException;
import com.kkimleang.authservice.model.Role;
import com.kkimleang.authservice.repository.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    @Transactional
    public Role findByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Role", "name", name)
                );
    }

    @Transactional
    public List<Role> findByNames(List<String> names) {
        return roleRepository.findByNameIn(names);
    }
}
