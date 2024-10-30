package com.kkimleang.authservice.dto.user;

import com.kkimleang.authservice.model.Role;
import java.util.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RoleDto {
    private UUID id;
    private String name;
    private Set<PermissionDto> permissions;

    public static Set<RoleDto> fromRoles(Set<Role> roles) {
        return roles.stream().map(role -> {
            RoleDto roleDto = new RoleDto();
            roleDto.setId(role.getId());
            roleDto.setName(role.getName());
            roleDto.setPermissions(PermissionDto.fromPermissions(role.getPermissions()));
            return roleDto;
        }).collect(java.util.stream.Collectors.toSet());
    }
}
