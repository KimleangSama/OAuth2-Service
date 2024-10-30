package com.kkimleang.authservice.dto.user;

import com.kkimleang.authservice.enumeration.AuthProvider;
import com.kkimleang.authservice.model.User;
import java.util.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserDto implements Serializable {
    private UUID id;
    private String username;
    private String email;
    private String profileURL;
    private AuthProvider provider;
    private Set<RoleDto> roles;

    public static UserDto fromUser(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        userDto.setProfileURL(user.getProfileURL());
        userDto.setProvider(user.getProvider());
        userDto.setRoles(RoleDto.fromRoles(user.getRoles()));
        return userDto;
    }

    public static List<UserDto> fromUsers(List<User> users) {
        return users.stream()
                .map(UserDto::fromUser)
                .toList();
    }
}
