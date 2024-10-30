package com.kkimleang.authservice.dto.user;

import com.kkimleang.authservice.model.Permission;
import java.util.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PermissionDto {
    private UUID id;
    private String name;

    public static Set<PermissionDto> fromPermissions(Set<Permission> permissions) {
        return permissions.stream().map(permission -> {
            PermissionDto permissionDto = new PermissionDto();
            permissionDto.setId(permission.getId());
            permissionDto.setName(permission.getName());
            return permissionDto;
        }).collect(java.util.stream.Collectors.toSet());
    }
}
