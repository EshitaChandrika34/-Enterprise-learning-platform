package com.enterpriselearning.service.impl;

import com.enterpriselearning.dto.request.LoginRequest;
import com.enterpriselearning.dto.request.RegisterRequest;
import com.enterpriselearning.dto.response.AuthResponse;
import com.enterpriselearning.dto.response.UserResponse;
import com.enterpriselearning.entity.Role;
import com.enterpriselearning.entity.User;
import com.enterpriselearning.exception.BadRequestException;
import com.enterpriselearning.exception.ResourceNotFoundException;
import com.enterpriselearning.repository.UserRepository;
import com.enterpriselearning.security.JwtTokenProvider;
import com.enterpriselearning.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(authentication);

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + loginRequest.getEmail()));

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .employeeId(user.getEmployeeId())
                .department(user.getDepartment())
                .role(user.getRole().name())
                .build();
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BadRequestException("Email already in use: " + registerRequest.getEmail());
        }

        String employeeId = registerRequest.getEmployeeId();
        if (employeeId == null || employeeId.trim().isEmpty()) {
            employeeId = "EMP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } else if (userRepository.existsByEmployeeId(employeeId)) {
            throw new BadRequestException("Employee ID already exists: " + employeeId);
        }

        Role role = registerRequest.getRole() != null ? registerRequest.getRole() : Role.EMPLOYEE;

        User user = User.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .phone(registerRequest.getPhone())
                .employeeId(employeeId)
                .department(registerRequest.getDepartment() != null ? registerRequest.getDepartment() : "General")
                .role(role)
                .build();

        User savedUser = userRepository.save(user);
        String token = jwtTokenProvider.generateTokenFromEmail(savedUser.getEmail());

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .id(savedUser.getId())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .email(savedUser.getEmail())
                .employeeId(savedUser.getEmployeeId())
                .department(savedUser.getDepartment())
                .role(savedUser.getRole().name())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .employeeId(user.getEmployeeId())
                .department(user.getDepartment())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
