package com.kkimleang.authservice.controller;

import com.kkimleang.authservice.dto.Response;
import com.kkimleang.authservice.dto.auth.AuthDto;
import com.kkimleang.authservice.dto.auth.LoginRequest;
import com.kkimleang.authservice.dto.auth.SignUpRequest;
import com.kkimleang.authservice.dto.user.UserDto;
import com.kkimleang.authservice.model.User;
import com.kkimleang.authservice.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.io.IOException;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private UserService userService;

    @PostMapping("/login")
    public Response<AuthDto> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            AuthDto response = userService.authenticateUser(loginRequest);
            return Response.<AuthDto>ok().setPayload(response);
        } catch (UsernameNotFoundException e) {
            return Response.<AuthDto>wrongCredentials()
                    .setErrors("User with email " + loginRequest.getEmail() + " not found.")
                    .setPayload(null);
        } catch (BadCredentialsException e) {
            return Response.<AuthDto>wrongCredentials()
                    .setErrors(e.getMessage())
                    .setPayload(null);
        } catch (Exception e) {
            return Response.<AuthDto>exception()
                    .setErrors("User authentication failed. " + e.getMessage())
                    .setPayload(null);
        }
    }

    @PostMapping("/signup")
    public Response<UserDto> signupUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        try {
            if (userService.existsByEmail(signUpRequest.getEmail())) {
                return Response.<UserDto>badRequest()
                        .setErrors("Email is already taken!")
                        .setPayload(null);
            }
            User user = userService.createUser(signUpRequest);
            return Response.<UserDto>created().setPayload(UserDto.fromUser(user));
        } catch (Exception e) {
            log.error("User registration failed. Reason: {}", e.getMessage());
            return Response.<UserDto>badRequest()
                    .setErrors("User registration failed. Reason: " + e.getMessage())
                    .setPayload(null);
        }
    }

    @GetMapping("/verify")
    public Response<UserDto> verifyUser(
            @RequestParam("code") String verificationCode
    ) {
        try {
            User user = userService.verifyUser(verificationCode);
            if (user != null) {
                return Response.<UserDto>ok()
                        .setPayload(UserDto.fromUser(user));
            } else {
                return Response.<UserDto>badRequest()
                        .setErrors("User verification failed.")
                        .setPayload(null);
            }
        } catch (Exception e) {
            return Response.<UserDto>badRequest()
                    .setErrors("User verification failed. " + e.getMessage())
                    .setPayload(null);
        }
    }

    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        userService.refreshToken(request, response);
    }
}