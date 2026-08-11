package com.mediclaim.mediclaim.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mediclaim.mediclaim.dto.auth.AuthResponse;
import com.mediclaim.mediclaim.dto.auth.LoginRequest;
import com.mediclaim.mediclaim.entity.User;
import com.mediclaim.mediclaim.entity.UserStatus;
import com.mediclaim.mediclaim.exception.BusinessException;
import com.mediclaim.mediclaim.repository.UserRepository;
import com.mediclaim.mediclaim.security.JwtService;

import org.springframework.transaction.annotation.Transactional;
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new BusinessException(
                                "Invalid email or password",
                                HttpStatus.UNAUTHORIZED
                        ));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BusinessException(
                    "User account is not active",
                    HttpStatus.FORBIDDEN
            );
        }

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new BusinessException(
                    "Invalid email or password",
                    HttpStatus.UNAUTHORIZED
            );
        }

        String token = jwtService.generateToken(
                user.getId(),
                user.getTenant().getId(),
                user.getRole().name()
        );

        return new AuthResponse(token);
    }
}