package com.kkimleang.authservice.controller;

import com.kkimleang.authservice.annotation.CurrentUser;
import com.kkimleang.authservice.dto.Response;
import com.kkimleang.authservice.dto.user.*;
import com.kkimleang.authservice.model.Permission;
import com.kkimleang.authservice.model.Role;
import com.kkimleang.authservice.model.User;
import com.kkimleang.authservice.service.user.CustomUserDetails;
import com.kkimleang.authservice.service.user.UserService;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth/user")
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public Response<UserDto> getCurrentUser(@CurrentUser CustomUserDetails currentUser) {
        try {
            User user = userService.findByEmail(currentUser.getEmail());
            UserDto userDto = UserDto.fromUser(user);
            return Response.<UserDto>ok()
                    .setPayload(userDto);
        } catch (Exception e) {
            return Response.<UserDto>exception()
                    .setErrors(e.getMessage());
        }
    }

    @GetMapping("/{id}/profile")
//    @PreAuthorize("hasRole('USER')")
    public Response<UserDto> getUserProfile(@PathVariable UUID id) {
        try {
            Optional<User> user = userService.findById(id);
            if (user.isEmpty()) {
                return Response.<UserDto>notFound()
                        .setErrors("User not found");
            }
            UserDto userDto = UserDto.fromUser(user.get());
            return Response.<UserDto>ok()
                    .setPayload(userDto);
        } catch (Exception e) {
            return Response.<UserDto>exception()
                    .setErrors(e.getMessage());
        }
    }

    @PostMapping("/profiles")
    public Response<List<UserDto>> getUserProfiles(@RequestBody List<UUID> ids) {
        try {
            List<User> users = userService.findByIds(ids);
            List<UserDto> userDtos = UserDto.fromUsers(users);
            return Response.<List<UserDto>>ok()
                    .setPayload(userDtos);
        } catch (Exception e) {
            return Response.<List<UserDto>>exception()
                    .setErrors(e.getMessage());
        }
    }

    @PutMapping("/update/profile")
    @PreAuthorize("hasRole('USER')")
    public Response<UserDto> updateUserProfile(
            @CurrentUser CustomUserDetails currentUser,
            @RequestBody UpdateProfileRequest request) {
        try {
            User user = userService.findByEmail(currentUser.getEmail());
            System.out.println(user.getId());
            if (!user.getId().equals(request.getId())) {
                return Response.<UserDto>accessDenied()
                        .setErrors("You are not allowed to update this user profile");
            }
            user.setProfileURL(request.getProfileURL());
            User updatedUser = userService.updateUserProfile(user);
            UserDto userDto = UserDto.fromUser(updatedUser);
            return Response.<UserDto>ok()
                    .setPayload(userDto);
        } catch (Exception e) {
            return Response.<UserDto>exception()
                    .setErrors(e.getMessage());
        }
    }
}
