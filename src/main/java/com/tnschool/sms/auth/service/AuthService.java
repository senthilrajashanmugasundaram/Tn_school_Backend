package com.tnschool.sms.auth.service;

import com.tnschool.sms.auth.dto.AuthUserResponse;
import com.tnschool.sms.auth.dto.ChangePasswordRequest;
import com.tnschool.sms.auth.dto.CurrentUserResponse;
import com.tnschool.sms.auth.dto.LoginRequest;
import com.tnschool.sms.auth.dto.LoginResponse;
import com.tnschool.sms.auth.model.UserEntity;
import com.tnschool.sms.auth.repository.UserRepository;
import com.tnschool.sms.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class AuthService {

    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public AuthService(JwtService jwtService, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        UserEntity user = userRepository.findByPhone(request.phone())
                .filter(UserEntity::isActive)
                .orElseThrow(() -> new IllegalArgumentException("Invalid phone or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid phone or password");
        }

        user.setLastLoginAt(OffsetDateTime.now());

        String authority = "ROLE_" + user.getRole().name();
        String accessToken = jwtService.generateAccessToken(user.getId(), List.of(authority));

        return new LoginResponse(
                accessToken,
                new AuthUserResponse(user.getId(), user.getName(), user.getPhone(), user.getRole().name())
        );
    }

    @Transactional(readOnly = true)
    public CurrentUserResponse getCurrentUser(String userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return new CurrentUserResponse(
                user.getId(),
                user.getName(),
                user.getPhone(),
                user.getEmail(),
                user.getRole().name()
        );
    }

    @Transactional
    public void changePassword(String userId, ChangePasswordRequest request) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
    }
}
