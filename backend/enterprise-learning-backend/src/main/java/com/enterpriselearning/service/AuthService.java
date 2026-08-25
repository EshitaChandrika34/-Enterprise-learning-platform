package com.enterpriselearning.service;

import com.enterpriselearning.dto.request.LoginRequest;
import com.enterpriselearning.dto.request.RegisterRequest;
import com.enterpriselearning.dto.response.AuthResponse;
import com.enterpriselearning.dto.response.UserResponse;

public interface AuthService {
    AuthResponse login(LoginRequest loginRequest);
    AuthResponse register(RegisterRequest registerRequest);
    UserResponse getCurrentUser(String email);
}
