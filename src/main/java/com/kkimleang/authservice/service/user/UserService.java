package com.kkimleang.authservice.service.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kkimleang.authservice.model.*;
import com.kkimleang.authservice.util.TokenProvider;
import com.kkimleang.authservice.dto.auth.AuthDto;
import com.kkimleang.authservice.dto.auth.LoginRequest;
import com.kkimleang.authservice.dto.auth.SignUpRequest;
import com.kkimleang.authservice.enumeration.AuthProvider;
import com.kkimleang.authservice.exception.ResourceNotFoundException;
import com.kkimleang.authservice.qpayload.UserSignUpVerification;
import com.kkimleang.authservice.repository.UserRepository;
import com.kkimleang.authservice.util.RandomString;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final VerificationCodeService verificationCodeService;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.email.name}")
    private String emailExchange;
    @Value("${rabbitmq.binding.email.name}")
    private String emailRoutingKey;

    @Cacheable(value = "user", key = "#email")
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User with " + email + " not found."));
    }

    public boolean existsByEmail(@NotBlank @Email String email) {
        return userRepository.existsByEmail(email);
    }

    public User createUser(SignUpRequest signUpRequest) {
        try {
            User user = new User();
            user.setUsername(signUpRequest.getUsername());
            user.setEmail(signUpRequest.getEmail());
            user.setPassword(signUpRequest.getPassword());
            // Setup roles following the request, if empty role, set default role to ROLE_USER
            if (signUpRequest.getRoles().isEmpty()) {
                Role userRole = roleService.findByName("ROLE_USER");
                user.getRoles().add(userRole);
            } else {
                signUpRequest.getRoles().forEach(role -> {
                    try {
                        List<Role> roles = roleService.findByNames(List.of(role));
                        user.getRoles().addAll(roles);
                    } catch (ResourceNotFoundException e) {
                        log.error("Role not found: {} with message: {}", role, e.getMessage());
                    }
                });
            }
            user.setProvider(AuthProvider.local);
            user.setIsEnabled(true);
            user.setIsVerified(false);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            // Return the saved user
            User signupUser = userRepository.save(user);
            String randomCode = RandomString.make(24);
            verificationCodeService.save(signupUser, randomCode);
            UserSignUpVerification verified = new UserSignUpVerification(
                    user.getEmail(),
                    user.getUsername(),
                    randomCode
            );
            rabbitTemplate.convertAndSend(emailExchange, emailRoutingKey, verified);
            return signupUser;
        } catch (Exception e) {
            log.error("Cannot create user with email: {}", signUpRequest.getEmail());
            throw new RuntimeException(e.getMessage());
        }
    }

    public AuthDto authenticateUser(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );
            if (authentication == null) {
                throw new BadCredentialsException("Username or password is incorrect.");
            }
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String accessToken = tokenProvider.createAccessToken(authentication);
            String refreshToken = tokenProvider.createRefreshToken(authentication);
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = userDetails.getUser();
            if (user == null) {
                throw new UsernameNotFoundException("User not found with email: " + loginRequest.getEmail());
            }
            return new AuthDto(
                    accessToken,
                    refreshToken,
                    user.getUsername(),
                    tokenProvider.getExpirationDateFromToken(accessToken)
            );
        } catch (Exception e) {
            String message = "We cannot authenticate user. Please check email and password.";
            log.error(message);
            throw new BadCredentialsException(message);
        }
    }

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = tokenProvider.getUserEmailFromToken(refreshToken);
        if (userEmail != null) {
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));
            if (tokenProvider.isTokenValid(refreshToken, user)) {
                var accessToken = tokenProvider.createAccessToken(user);
                var authResponse = new AuthDto(
                        accessToken,
                        refreshToken,
                        user.getUsername(),
                        tokenProvider.getExpirationDateFromToken(accessToken)
                );
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    public User verifyUser(String verificationCode) {
        try {
            User user = verificationCodeService.findByCode(verificationCode);
            if (user != null) {
                user.setIsVerified(true);
                user = userRepository.save(user);
                return user;
            }
            throw new ResourceNotFoundException("User", "verification code", verificationCode);
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            throw new RuntimeException("Cannot verify user with code: " + verificationCode);
        }
    }

    public Optional<User> findById(UUID id) {
        try {
            return userRepository.findById(id);
        } catch (Exception e) {
            throw new ResourceNotFoundException("User", "id", id);
        }
    }

    public List<User> findByIds(List<UUID> ids) {
        try {
            return userRepository.findByIdIn(ids);
        } catch (Exception e) {
            throw new ResourceNotFoundException("User", "id", ids);
        }
    }

    @CachePut(value = "user", key = "#user.email")
    @Transactional
    public User updateUserProfile(User user) {
        try {
            Integer updated = userRepository.updateUserProfile(user.getId(), user.getProfileURL());
            if (updated == 1) {
                return user;
            } else {
                throw new RuntimeException("Cannot update user profile with id: " + user.getId());
            }
        } catch (Exception e) {
            log.error("Exception Error: {}", e.getMessage());
            throw new ResourceNotFoundException("User", "id", user.getId());
        }
    }
}
