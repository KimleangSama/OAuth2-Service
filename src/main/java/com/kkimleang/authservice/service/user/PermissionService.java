package com.kkimleang.authservice.service.user;

import com.kkimleang.authservice.model.Permission;
import com.kkimleang.authservice.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PermissionService {
    private final PermissionRepository permissionRepository;

    public Permission findByName(String name) {
        return permissionRepository.findByName(name);
    }
}
